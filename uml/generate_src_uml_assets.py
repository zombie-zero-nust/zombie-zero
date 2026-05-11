#!/usr/bin/env python3
"""Generate Mermaid UML files and optionally render PNG/PDF assets.

This is the orchestration entrypoint. The work is split into two reusable parts:
- generate_src_mermaid.py: scan Java sources and write .mmd files
- render_mermaid_assets.py: convert Mermaid files into PNG/PDF assets

Run from the repository root:
    python uml/generate_src_uml_assets.py --skip-render
    python uml/generate_src_uml_assets.py
    python uml/generate_src_uml_assets.py --target-dir src/game/java/edu/nust/game/systems
"""

import argparse
from pathlib import Path


def build_parser():
    script_dir = Path(__file__).resolve().parent
    default_output = script_dir / "generated"
    default_source = script_dir.parent / "src"

    parser = argparse.ArgumentParser(
        description="Generate package-aware Mermaid UML and render PNG/PDF assets.",
        epilog=(
            "Examples:\n"
            "  python3 uml/generate_src_uml_assets.py --skip-render\n"
            "  python3 uml/generate_src_uml_assets.py\n"
            "  python3 uml/generate_src_uml_assets.py --source-dir src --output-dir uml/generated\n"
            "  python3 uml/generate_src_uml_assets.py --formats png"
        ),
        formatter_class=argparse.RawDescriptionHelpFormatter,
    )
    parser.add_argument(
        "--source-dir",
        type=Path,
        default=default_source,
        help="Source tree to scan for Java files.",
    )
    parser.add_argument(
        "--output-dir",
        type=Path,
        default=default_output,
        help="Directory for generated .mmd/.pdf/.png files.",
    )
    parser.add_argument(
        "--threshold",
        type=int,
        default=24,
        help="Maximum classes per leaf diagram before splitting deeper.",
    )
    parser.add_argument(
        "--fields", action="store_true", help="Include fields in class diagrams."
    )
    parser.add_argument(
        "--methods", action="store_true", help="Include methods in class diagrams."
    )
    parser.add_argument(
        "--skip-render", action="store_true", help="Generate .mmd files only."
    )
    parser.add_argument(
        "--target-dir",
        type=str,
        help="Generate diagram for this specific directory only.",
    )
    parser.add_argument(
        "--output-file",
        type=str,
        default=None,
        help="Base name for output file when using --target-dir (without extension).",
    )
    parser.add_argument(
        "--formats",
        nargs="+",
        choices=("png", "pdf"),
        default=["png", "pdf"],
        help="Rendered output formats.",
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


def build_generation_args(args):
    return argparse.Namespace(
        source_dir=args.source_dir,
        output_dir=args.output_dir,
        threshold=args.threshold,
        fields=args.fields,
        methods=args.methods,
        target_dir=args.target_dir,
        output_file=args.output_file,
    )


def build_render_args(args):
    return argparse.Namespace(
        input_path=[args.output_dir.resolve() / "mmd"],
        output_dir=args.output_dir,
        formats=args.formats,
        clean=True,
        theme=args.theme,
        background_color=args.background_color,
        scale=args.scale,
        width=args.width,
        height=args.height,
        max_text_size=args.max_text_size,
        max_edges=args.max_edges,
    )


def main(argv=None):
    from generate_src_mermaid import write_mermaid_files
    from render_mermaid_assets import render_from_args

    args = parse_args(argv)

    generation_status = write_mermaid_files(build_generation_args(args))
    if generation_status != 0 or args.skip_render:
        return generation_status

    return render_from_args(build_render_args(args))


if __name__ == "__main__":
    raise SystemExit(main())