#!/usr/bin/env python3
"""Render Mermaid files into PNG and PDF assets.

Run from the repository root:
    python uml/render_mermaid_assets.py
    python uml/render_mermaid_assets.py --input-path uml/generated/mmd --formats png
    python uml/render_mermaid_assets.py --input-path uml/generated/mmd/package_edu_nust_game.mmd --formats pdf
"""

import argparse
import json
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

SUPPORTED_SUFFIXES = {".mmd", ".mermaid"}
SUPPORTED_FORMATS = ("png", "pdf")


def find_mermaid_files(input_paths):
    files = []
    seen = set()
    for raw_path in input_paths:
        path = raw_path.resolve()
        if not path.exists():
            print(f"Error: {path} does not exist", file=sys.stderr)
            return None
        if path.is_file():
            if path.suffix.lower() not in SUPPORTED_SUFFIXES:
                print(
                    f"Error: {path} is not a supported Mermaid file",
                    file=sys.stderr,
                )
                return None
            if path not in seen:
                files.append(path)
                seen.add(path)
            continue

        if not path.is_dir():
            print(f"Error: {path} is not a file or directory", file=sys.stderr)
            return None

        for child in sorted(path.rglob("*")):
            if child.is_file() and child.suffix.lower() in SUPPORTED_SUFFIXES:
                resolved = child.resolve()
                if resolved not in seen:
                    files.append(resolved)
                    seen.add(resolved)

    return files


def find_mermaid_cli():
    resolved = shutil.which("mmdc")
    if resolved:
        return [resolved]
    if shutil.which("npx"):
        return ["npx", "-y", "@mermaid-js/mermaid-cli"]
    raise RuntimeError(
        "Mermaid CLI is not available. Install mmdc or ensure npx is on PATH."
    )


def create_mermaid_config(max_text_size, max_edges):
    config = {
        "look": "classic",
        "layout": "elk",
        "flowchart": {"curve": "linear"},
        "class": {"defaultRenderer": "dagre-wrapper"},
        "maxTextSize": max_text_size,
        "maxEdges": max_edges,
    }
    handle = tempfile.NamedTemporaryFile(
        "w", encoding="utf-8", suffix=".json", delete=False
    )
    with handle:
        json.dump(config, handle)
    return Path(handle.name)


def build_render_command(cli_command, source_file, output_file, args, config_file):
    command = [
        *cli_command,
        "-i",
        str(source_file),
        "-o",
        str(output_file),
        "-c",
        str(config_file),
    ]
    if args.theme:
        command.extend(["-t", args.theme])
    if args.scale is not None:
        command.extend(["-s", str(args.scale)])
    if args.width is not None:
        command.extend(["-w", str(args.width)])
    if args.height is not None:
        command.extend(["-H", str(args.height)])
    if output_file.suffix.lower() == ".png":
        command.extend(["-b", args.background_color])
    return command


def clean_output_dirs(output_root, formats):
    for file_format in formats:
        format_dir = output_root / file_format
        if format_dir.exists():
            shutil.rmtree(format_dir)


def render_diagrams(mmd_files, output_root, args):
    cli_command = find_mermaid_cli()
    output_root.mkdir(parents=True, exist_ok=True)
    if args.clean:
        clean_output_dirs(output_root, args.formats)

    destination_dirs = {}
    for file_format in args.formats:
        destination_dir = output_root / file_format
        destination_dir.mkdir(parents=True, exist_ok=True)
        destination_dirs[file_format] = destination_dir

    config_file = create_mermaid_config(args.max_text_size, args.max_edges)
    rendered = []
    try:
        for mmd_file in mmd_files:
            for file_format in args.formats:
                output_file = destination_dirs[file_format] / f"{mmd_file.stem}.{file_format}"
                command = build_render_command(
                    cli_command, mmd_file, output_file, args, config_file
                )
                result = subprocess.run(command, capture_output=True, text=True)
                if result.returncode != 0:
                    stderr = result.stderr.strip() or result.stdout.strip() or "Unknown error"
                    raise RuntimeError(
                        f"Failed to render {mmd_file.name} to {file_format}: {stderr}"
                    )
                rendered.append(output_file)
    finally:
        config_file.unlink(missing_ok=True)

    return rendered


def render_from_args(args):
    input_paths = args.input_path or []
    mmd_files = find_mermaid_files(input_paths)
    if mmd_files is None:
        return 1
    if not mmd_files:
        print("No Mermaid files found. Nothing to render.", file=sys.stderr)
        return 1

    print(f"Found {len(mmd_files)} Mermaid file(s) to render.")
    try:
        rendered_files = render_diagrams(mmd_files, args.output_dir.resolve(), args)
    except Exception as exc:
        print(f"Error: {exc}", file=sys.stderr)
        return 1

    print(f"Rendered {len(rendered_files)} asset file(s) in {args.output_dir.resolve()}.")
    return 0


def build_parser():
    script_dir = Path(__file__).resolve().parent
    default_output = script_dir / "generated"
    default_input = default_output / "mmd"

    parser = argparse.ArgumentParser(
        description="Render Mermaid files into PNG and PDF assets.",
        epilog=(
            "Examples:\n"
            "  python3 uml/render_mermaid_assets.py\n"
            "  python3 uml/render_mermaid_assets.py --input-path uml/generated/mmd --formats png\n"
            "  python3 uml/render_mermaid_assets.py --input-path uml/generated/mmd/package_edu_nust_game.mmd --formats pdf"
        ),
        formatter_class=argparse.RawDescriptionHelpFormatter,
    )
    parser.add_argument(
        "--input-path",
        type=Path,
        nargs="+",
        default=[default_input],
        help="One or more Mermaid files or directories to render.",
    )
    parser.add_argument(
        "--output-dir",
        type=Path,
        default=default_output,
        help="Directory for rendered PNG/PDF assets.",
    )
    parser.add_argument(
        "--formats",
        nargs="+",
        choices=SUPPORTED_FORMATS,
        default=list(SUPPORTED_FORMATS),
        help="Rendered output formats.",
    )
    parser.add_argument(
        "--clean",
        action="store_true",
        help="Remove existing format directories before rendering.",
    )
    parser.add_argument("--theme", help="Optional Mermaid theme for rendering.")
    parser.add_argument(
        "--background-color", default="white", help="PNG background color."
    )
    parser.add_argument("--scale", type=float, help="Optional Mermaid CLI scale.")
    parser.add_argument("--width", type=int, help="Optional Mermaid CLI width.")
    parser.add_argument("--height", type=int, help="Optional Mermaid CLI height.")
    parser.add_argument(
        "--max-text-size",
        type=int,
        default=1_000_000,
        help="Mermaid maxTextSize override.",
    )
    parser.add_argument(
        "--max-edges", type=int, default=100_000, help="Mermaid maxEdges override."
    )
    return parser


def parse_args(argv=None):
    return build_parser().parse_args(argv)


def main(argv=None):
    return render_from_args(parse_args(argv))


if __name__ == "__main__":
    raise SystemExit(main())