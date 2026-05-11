#!/usr/bin/env python3
"""Generate Mermaid UML diagrams for the project's Java source tree.

Run from the repository root:
    python uml/generate_src_mermaid.py
    python uml/generate_src_mermaid.py --target-dir src/game/java/edu/nust/game/systems
    python uml/generate_src_mermaid.py --output-dir uml/generated
"""

import argparse
import os
import re
import sys
from collections import defaultdict, deque
from pathlib import Path

try:
    import javalang
    from javalang.tree import ClassDeclaration, EnumDeclaration, InterfaceDeclaration
    _JAVALANG_IMPORT_ERROR = None
except ModuleNotFoundError as exc:
    javalang = None
    ClassDeclaration = type(None)
    EnumDeclaration = type(None)
    InterfaceDeclaration = type(None)
    _JAVALANG_IMPORT_ERROR = exc

RECORD_PATTERN = re.compile(
    r"(?P<prefix>(?:public|protected|private|abstract|static|final|strictfp|sealed|non-sealed)\s+)*"
    r"record\s+(?P<name>\w+)\s*\((?P<components>[^)]*)\)"
    r"(?P<suffix>\s*(?:implements\s+[^{]+)?)\s*\{",
    re.MULTILINE,
)
CONTROL_KEYWORDS = {
    "if",
    "for",
    "while",
    "switch",
    "catch",
    "synchronized",
    "try",
    "else",
    "do",
}
TYPE_KEYWORDS = {"class", "interface", "enum"}


def ensure_javalang_available():
    if _JAVALANG_IMPORT_ERROR is not None:
        raise RuntimeError(
            "Missing Python dependency 'javalang'. Install it with: python -m pip install javalang"
        ) from _JAVALANG_IMPORT_ERROR


def find_java_files(root_dir):
    for dirpath, _, filenames in os.walk(root_dir):
        for fname in filenames:
            if fname.endswith(".java"):
                yield os.path.join(dirpath, fname)


def _convert_record(match):
    prefix = match.group("prefix") or ""
    prefix = re.sub(r"\b(?:sealed|non-sealed)\b\s*", "", prefix)
    name = match.group("name")
    components = match.group("components").strip()
    suffix = match.group("suffix") or ""

    fields = []
    if components:
        for component in components.split(","):
            component = component.strip()
            if not component:
                continue
            field_type, field_name = component.rsplit(" ", 1)
            fields.append(f"    private {field_type} {field_name};")

    body = "\n".join(fields)
    if body:
        body = "\n" + body + "\n"
    return f"{prefix}class {name}{suffix} {{{body}"


def strip_method_bodies(source):
    result = []
    index = 0
    length = len(source)
    paren_depth = 0
    angle_depth = 0
    string_delim = None
    escape = False
    line_comment = False
    block_comment = False
    pending_header = False
    header_token = None
    seen_paren_in_header = False
    last_word = ""

    while index < length:
        char = source[index]
        next_char = source[index + 1] if index + 1 < length else ""

        if line_comment:
            result.append(char)
            if char == "\n":
                line_comment = False
            index += 1
            continue

        if block_comment:
            result.append(char)
            if char == "*" and next_char == "/":
                result.append(next_char)
                index += 2
                block_comment = False
            else:
                index += 1
            continue

        if string_delim:
            result.append(char)
            if escape:
                escape = False
            elif char == "\\":
                escape = True
            elif char == string_delim:
                string_delim = None
            index += 1
            continue

        if char == "/" and next_char == "/":
            result.extend([char, next_char])
            index += 2
            line_comment = True
            continue

        if char == "/" and next_char == "*":
            result.extend([char, next_char])
            index += 2
            block_comment = True
            continue

        if char in {'"', "'"}:
            result.append(char)
            string_delim = char
            index += 1
            continue

        if char == "@":
            index += 1
            while index < length and (
                source[index].isalnum() or source[index] in {"_", ".", "$"}
            ):
                index += 1
            if index < length and source[index] == "(":
                depth = 1
                index += 1
                while index < length and depth > 0:
                    if source[index] == "(":
                        depth += 1
                    elif source[index] == ")":
                        depth -= 1
                    elif source[index] in {'"', "'"}:
                        quote = source[index]
                        index += 1
                        while index < length:
                            if source[index] == "\\":
                                index += 2
                                continue
                            if source[index] == quote:
                                break
                            index += 1
                    index += 1
            while index < length and source[index].isspace() and source[index] != "\n":
                index += 1
            continue

        if char.isalpha() or char == "_" or (last_word and char.isdigit()):
            start = index
            while index < length and (
                source[index].isalnum() or source[index] in {"_", "$", "-"}
            ):
                index += 1
            word = source[start:index]
            result.append(word)
            last_word = word

            if word in TYPE_KEYWORDS or word == "record":
                pending_header = False
                header_token = word
                seen_paren_in_header = False
            elif paren_depth == 0 and angle_depth == 0 and word not in CONTROL_KEYWORDS:
                pending_header = True
                header_token = word
            continue

        if char == "<" and not pending_header:
            angle_depth += 1
        elif char == ">" and angle_depth > 0 and not pending_header:
            angle_depth -= 1
        elif char == "(":
            paren_depth += 1
            if pending_header:
                seen_paren_in_header = True
        elif char == ")":
            paren_depth -= 1
        elif char == ";":
            pending_header = False
            seen_paren_in_header = False
            header_token = None
        elif char == "{":
            should_strip = (
                pending_header
                and seen_paren_in_header
                and header_token not in CONTROL_KEYWORDS | TYPE_KEYWORDS
            )
            result.append(char)
            index += 1
            if should_strip:
                depth = 1
                while index < length and depth > 0:
                    current = source[index]
                    next_inner = source[index + 1] if index + 1 < length else ""
                    if current == "/" and next_inner == "/":
                        index += 2
                        while index < length and source[index] != "\n":
                            index += 1
                        continue
                    if current == "/" and next_inner == "*":
                        index += 2
                        while index + 1 < length and not (
                            source[index] == "*" and source[index + 1] == "/"
                        ):
                            index += 1
                        index += 2
                        continue
                    if current in {'"', "'"}:
                        quote = current
                        index += 1
                        while index < length:
                            if source[index] == "\\":
                                index += 2
                                continue
                            if source[index] == quote:
                                index += 1
                                break
                            index += 1
                        continue
                    if current == "{":
                        depth += 1
                    elif current == "}":
                        depth -= 1
                    index += 1
                result.append("}")
                pending_header = False
                seen_paren_in_header = False
                header_token = None
                continue

            pending_header = False
            seen_paren_in_header = False
            header_token = None
            last_word = ""
            continue
        elif char == "}":
            pending_header = False
            seen_paren_in_header = False
            header_token = None

        result.append(char)
        index += 1

    return "".join(result)


def sanitize_java_source(source):
    source = source.replace("\n///", "\n//")
    if source.startswith("///"):
        source = "//" + source[3:]

    source = re.sub(r"\bnon-sealed\b\s*", "", source)
    source = re.sub(r"\bsealed\b\s+", "", source)
    source = re.sub(r"\s+permits\s+[^{]+(?=\{)", "", source)
    source = re.sub(
        r"\binstanceof\s+([A-Z][\w$.<>?, ]+)\s+[a-zA-Z_$][\w$]*",
        r"instanceof \1",
        source,
    )
    source = RECORD_PATTERN.sub(_convert_record, source)
    source = re.sub(
        r"\b(public|protected|private)\s+([A-Z][\w$]*)\s*\{", r"\1 \2() {", source
    )
    return strip_method_bodies(source)


def parse_java_file(filepath):
    try:
        ensure_javalang_available()
        with open(filepath, "r", encoding="utf-8") as handle:
            source = sanitize_java_source(handle.read())
            return javalang.parse.parse(source)
    except Exception as exc:
        description = getattr(exc, "description", str(exc)) or type(exc).__name__
        location = getattr(exc, "at", None)
        if location is not None:
            print(f"  [WARN] Could not parse {filepath}: {description} at {location}")
        else:
            print(f"  [WARN] Could not parse {filepath}: {description}")
        return None


def get_full_name(package, name):
    return f"{package}.{name}" if package else name


def resolve_type(ref_type, imports, current_package):
    ensure_javalang_available()
    if ref_type is None:
        return None
    if isinstance(ref_type, javalang.tree.BasicType):
        return ref_type.name
    if isinstance(ref_type, javalang.tree.ReferenceType):
        base = ref_type.name
        if "." in base:
            return base
        for imp in imports:
            if imp.endswith("." + base) or imp.endswith("." + base + ".*"):
                if imp.endswith(".*"):
                    return imp[:-2] + "." + base
                return imp
        if current_package:
            return f"{current_package}.{base}"
        return base
    return None


def extract_info(tree, filepath):
    ensure_javalang_available()
    if tree is None:
        return []
    package = tree.package.name if tree.package else ""
    imports = [imp.path for imp in tree.imports] if tree.imports else []
    entities = []
    for _, node in tree.filter(javalang.tree.TypeDeclaration):
        if not isinstance(
            node, (ClassDeclaration, InterfaceDeclaration, EnumDeclaration)
        ):
            continue
        name = node.name
        full_name = get_full_name(package, name)
        kind = type(node).__name__.replace("Declaration", "")
        super_class = None
        interfaces = []
        if isinstance(node, ClassDeclaration):
            super_class = node.extends.name if node.extends else None
            interfaces = (
                [interface.name for interface in node.implements]
                if node.implements
                else []
            )
        elif isinstance(node, InterfaceDeclaration):
            interfaces = (
                [interface.name for interface in node.extends] if node.extends else []
            )

        fields = []
        methods = []
        for member in node.body:
            if isinstance(member, javalang.tree.FieldDeclaration):
                for declarator in member.declarators:
                    field_type = resolve_type(member.type, imports, package)
                    fields.append((declarator.name, field_type))
            elif isinstance(member, javalang.tree.MethodDeclaration):
                params = []
                for param in member.parameters:
                    params.append(
                        (param.name, resolve_type(param.type, imports, package))
                    )
                return_type = (
                    resolve_type(member.return_type, imports, package)
                    if member.return_type
                    else "void"
                )
                methods.append((member.name, return_type, params))

        entities.append(
            {
                "full_name": full_name,
                "name": name,
                "kind": kind,
                "super": super_class,
                "interfaces": interfaces,
                "fields": fields,
                "methods": methods,
                "imports": imports,
                "package": package,
                "file": filepath,
            }
        )
    return entities


def build_knowledge(entities):
    known = {}
    simple_to_full = defaultdict(list)
    for entity in entities:
        known[entity["full_name"]] = entity
        simple_to_full[entity["name"]].append(entity["full_name"])
    return known, simple_to_full


def resolve_full_class(name, entity, known, simple_to_full):
    if name is None:
        return None
    if name in known:
        return name
    for imp in entity["imports"]:
        if imp.endswith("." + name) and imp in known:
            return imp
        if imp.endswith(".*"):
            candidate = f"{imp[:-2]}.{name}"
            if candidate in known:
                return candidate
    if entity["package"]:
        candidate = f"{entity['package']}.{name}"
        if candidate in known:
            return candidate
    candidates = simple_to_full.get(name, [])
    if len(candidates) == 1:
        return candidates[0]
    return None


def build_graph(entities, known, simple_to_full):
    graph = defaultdict(set)
    all_nodes = set(known.keys())
    for entity in entities:
        src = entity["full_name"]
        if entity["super"]:
            sup = resolve_full_class(entity["super"], entity, known, simple_to_full)
            if sup and sup in all_nodes:
                graph[src].add(sup)
        for interface in entity["interfaces"]:
            iface_full = resolve_full_class(interface, entity, known, simple_to_full)
            if iface_full and iface_full in all_nodes:
                graph[src].add(iface_full)
        for _, field_type in entity["fields"]:
            if field_type:
                resolved = resolve_full_class(field_type, entity, known, simple_to_full)
                if resolved and resolved in all_nodes and resolved != src:
                    graph[src].add(resolved)
        for _, return_type, params in entity["methods"]:
            if return_type and return_type != "void":
                resolved = resolve_full_class(
                    return_type, entity, known, simple_to_full
                )
                if resolved and resolved in all_nodes and resolved != src:
                    graph[src].add(resolved)
            for _, param_type in params:
                if param_type:
                    resolved = resolve_full_class(
                        param_type, entity, known, simple_to_full
                    )
                    if resolved and resolved in all_nodes and resolved != src:
                        graph[src].add(resolved)
    return graph


def topological_sort(graph):
    in_degree = {node: 0 for node in graph}
    for _, targets in graph.items():
        for tgt in targets:
            in_degree[tgt] = in_degree.get(tgt, 0) + 1
    queue = deque([node for node, degree in in_degree.items() if degree == 0])
    sorted_nodes = []
    while queue:
        node = queue.popleft()
        sorted_nodes.append(node)
        for target in graph.get(node, []):
            in_degree[target] -= 1
            if in_degree[target] == 0:
                queue.append(target)
    for node, degree in in_degree.items():
        if degree > 0 and node not in sorted_nodes:
            sorted_nodes.append(node)
    return sorted_nodes


def build_mermaid_name_maps(entities, simple_to_full):
    alias_map = {}
    used_aliases = {}
    for full_name, entity in entities.items():
        if len(simple_to_full[entity["name"]]) == 1:
            base_alias = entity["name"]
        else:
            base_alias = re.sub(r"\W+", "_", full_name)
        alias = base_alias
        counter = 2
        while alias in used_aliases and used_aliases[alias] != full_name:
            alias = f"{base_alias}_{counter}"
            counter += 1
        used_aliases[alias] = full_name
        alias_map[full_name] = alias
    return alias_map


def sanitize_package_filename(package_name):
    return re.sub(r"\W+", "_", package_name)


def build_class_declaration(entity, alias, include_fields, include_methods):
    stereotype = ""
    if entity["kind"] == "Interface":
        stereotype = " <<interface>>"
    elif entity["kind"] == "Enum":
        stereotype = " <<enumeration>>"

    decl = f"class {alias}{stereotype}"
    if include_fields or include_methods:
        decl += " {"
        members = []
        if include_fields:
            for field_name, field_type in entity["fields"]:
                short_type = field_type.split(".")[-1] if field_type else "Object"
                members.append(f"  +{short_type} {field_name}")
        if include_methods:
            for method_name, return_type, params in entity["methods"]:
                param_str = ", ".join(
                    f"{param_type.split('.')[-1] if param_type else 'Object'} {param_name}"
                    for param_name, param_type in params
                )
                short_return = (
                    return_type.split(".")[-1]
                    if return_type and return_type != "void"
                    else "void"
                )
                members.append(f"  +{short_return} {method_name}({param_str})")
        decl += "\n" + "\n".join(members) + "\n}"
    return decl


def build_subset_simple_to_full(selected_nodes, known):
    simple_to_full = defaultdict(list)
    for full_name in selected_nodes:
        simple_to_full[known[full_name]["name"]].append(full_name)
    return simple_to_full


def topological_sort_subset(graph, selected_nodes):
    subset_graph = {
        node: {target for target in graph.get(node, set()) if target in selected_nodes}
        for node in selected_nodes
        if node in graph or any(node in targets for targets in graph.values())
    }
    sorted_nodes = [
        node for node in topological_sort(subset_graph) if node in selected_nodes
    ]
    remainder = sorted(node for node in selected_nodes if node not in sorted_nodes)
    return sorted_nodes + remainder


def mermaid_header(title):
    return [
        "---",
        f"title: {title}",
        "config:",
        "  look: classic",
        "  layout: elk",
        "  flowchart:",
        "    curve: linear",
        "  class:",
        "    defaultRenderer: dagre-wrapper",
        "---",
        "classDiagram",
    ]


def generate_leaf_mermaid(
    package_name,
    selected_nodes,
    known,
    graph,
    include_fields,
    include_methods,
    root_package=None,
):
    if not selected_nodes:
        return "\n".join(
            mermaid_header(package_name or "Default Package")
            + ["%% No classes in selection"]
        )

    effective_root = root_package if root_package is not None else package_name

    selected_entities = {name: known[name] for name in selected_nodes}
    simple_to_full = build_subset_simple_to_full(selected_nodes, known)
    alias_map = build_mermaid_name_maps(selected_entities, simple_to_full)

    title = package_name if package_name else "Default Package"
    lines = mermaid_header(title)

    topo_order = topological_sort_subset(graph, selected_nodes)
    nodes_by_package = defaultdict(list)
    for full_name in topo_order:
        pkg = known[full_name]["package"]
        nodes_by_package[pkg].append(full_name)

    def _pkg_sort_key(item):
        return "" if item[0] == effective_root else item[0]

    for pkg, full_names in sorted(nodes_by_package.items(), key=_pkg_sort_key):
        is_sub_package = pkg != effective_root
        if is_sub_package:
            lines.append(f"namespace {pkg} {{")
        for full_name in full_names:
            lines.append(
                "  "
                + build_class_declaration(
                    known[full_name],
                    alias_map[full_name],
                    include_fields,
                    include_methods,
                )
            )
        if is_sub_package:
            lines.append("}")

    for src in sorted(selected_nodes):
        for tgt in sorted(
            target for target in graph.get(src, set()) if target in selected_nodes
        ):
            src_entity = known[src]
            tgt_entity = known[tgt]
            src_alias = alias_map[src]
            tgt_alias = alias_map[tgt]
            if tgt_entity["kind"] == "Interface":
                resolved_interfaces = [
                    resolve_full_class(
                        interface, src_entity, selected_entities, simple_to_full
                    )
                    for interface in src_entity["interfaces"]
                ]
                if tgt in resolved_interfaces:
                    lines.append(f"{src_alias} ..|> {tgt_alias} : implements")
                else:
                    lines.append(f"{src_alias} ..> {tgt_alias} : uses")
            else:
                super_full = resolve_full_class(
                    src_entity.get("super"),
                    src_entity,
                    selected_entities,
                    simple_to_full,
                )
                if super_full == tgt:
                    lines.append(f"{tgt_alias} <|-- {src_alias} : extends")
                else:
                    lines.append(f"{src_alias} --> {tgt_alias} : uses")

    return "\n".join(lines)


def build_package_maps(entities):
    package_entities = defaultdict(set)
    package_children = defaultdict(set)
    packages = set()
    for entity in entities:
        package = entity["package"]
        package_entities[package].add(entity["full_name"])
        segments = package.split(".")
        for index in range(len(segments)):
            current = ".".join(segments[: index + 1])
            packages.add(current)
            if index > 0:
                parent = ".".join(segments[:index])
                package_children[parent].add(current)
    return package_entities, package_children, packages


def build_subtree_cache(packages, package_entities, package_children):
    subtree_cache = {}

    def collect(package_name):
        if package_name in subtree_cache:
            return subtree_cache[package_name]
        nodes = set(package_entities.get(package_name, set()))
        for child in package_children.get(package_name, set()):
            nodes.update(collect(child))
        subtree_cache[package_name] = nodes
        return nodes

    for package in sorted(packages, key=lambda item: item.count("."), reverse=True):
        collect(package)
    return subtree_cache


def package_id(package_name):
    return f"Package_{sanitize_package_filename(package_name)}"


def package_stereotype(package_name, subtree_nodes):
    del subtree_nodes
    return f"class {package_id(package_name)} <<package>>"


def summarize_branch_edges(
    package_name, direct_classes, child_packages, subtree_cache, graph
):
    del package_name
    lines = []
    direct_class_set = set(direct_classes)

    for src in sorted(direct_class_set):
        for tgt in sorted(
            target for target in graph.get(src, set()) if target in direct_class_set
        ):
            lines.append((src, tgt, "class"))

    for child in sorted(child_packages):
        child_nodes = subtree_cache[child]
        has_outbound = any(
            target in child_nodes
            for src in direct_class_set
            for target in graph.get(src, set())
        )
        if has_outbound:
            for src in sorted(direct_class_set):
                if any(target in child_nodes for target in graph.get(src, set())):
                    lines.append((src, child, "class_to_package"))

        for dst in sorted(direct_class_set):
            if any(dst in graph.get(src, set()) for src in child_nodes):
                lines.append((child, dst, "package_to_class"))

    for child_src in sorted(child_packages):
        source_nodes = subtree_cache[child_src]
        for child_tgt in sorted(pkg for pkg in child_packages if pkg != child_src):
            target_nodes = subtree_cache[child_tgt]
            if any(
                target in target_nodes
                for src in source_nodes
                for target in graph.get(src, set())
            ):
                lines.append((child_src, child_tgt, "package_to_package"))

    return lines


def generate_branch_mermaid(
    package_name,
    direct_classes,
    child_packages,
    known,
    graph,
    include_fields,
    include_methods,
    subtree_cache,
):
    selected_entities = {name: known[name] for name in direct_classes}
    simple_to_full = build_subset_simple_to_full(direct_classes, known)
    alias_map = (
        build_mermaid_name_maps(selected_entities, simple_to_full)
        if direct_classes
        else {}
    )

    title = package_name if package_name else "Default Package"
    lines = mermaid_header(title)

    for full_name in topological_sort_subset(graph, direct_classes):
        lines.append(
            "  "
            + build_class_declaration(
                known[full_name], alias_map[full_name], include_fields, include_methods
            )
        )

    for child in sorted(child_packages):
        lines.append(package_stereotype(child, subtree_cache[child]))

    for source, target, edge_kind in summarize_branch_edges(
        package_name, direct_classes, child_packages, subtree_cache, graph
    ):
        if edge_kind == "class":
            lines.append(f"{alias_map[source]} --> {alias_map[target]} : uses")
        elif edge_kind == "class_to_package":
            lines.append(f"{alias_map[source]} --> {package_id(target)} : uses")
        elif edge_kind == "package_to_class":
            lines.append(f"{package_id(source)} --> {alias_map[target]} : uses")
        else:
            lines.append(f"{package_id(source)} --> {package_id(target)} : uses")

    if len(lines) == len(mermaid_header(title)):
        lines.append("%% No classes or child packages to show")

    return "\n".join(lines)


def plan_package_diagrams(
    packages, package_entities, package_children, subtree_cache, threshold
):
    diagrams = []

    def visit(package_name):
        subtree_nodes = subtree_cache.get(package_name, set())
        if not subtree_nodes:
            return
        if len(subtree_nodes) <= threshold:
            diagrams.append((package_name, "leaf", set(subtree_nodes), set()))
            return
        diagrams.append(
            (
                package_name,
                "branch",
                set(package_entities.get(package_name, set())),
                set(package_children.get(package_name, set())),
            )
        )
        for child in sorted(package_children.get(package_name, set())):
            visit(child)

    top_level_packages = sorted(package for package in packages if "." not in package)
    for package in top_level_packages:
        visit(package)
    return diagrams


def clean_output(output_root):
    mmd_dir = output_root / "mmd"
    if mmd_dir.exists():
        for existing in mmd_dir.iterdir():
            if existing.is_file():
                existing.unlink()
    output_root.mkdir(parents=True, exist_ok=True)


def resolve_target_dir(source_dir, target_dir):
    raw_target = Path(target_dir)
    if raw_target.is_absolute():
        return raw_target.resolve()

    source_relative = (source_dir / raw_target).resolve()
    if source_relative.is_dir():
        return source_relative

    cwd_relative = (Path.cwd() / raw_target).resolve()
    if cwd_relative.is_dir():
        return cwd_relative

    return source_relative


def write_mermaid_files(args):
    try:
        ensure_javalang_available()
    except RuntimeError as exc:
        print(f"Error: {exc}", file=sys.stderr)
        return 1

    source_dir = args.source_dir.resolve()
    output_root = args.output_dir.resolve()
    mmd_dir = output_root / "mmd"

    if not source_dir.is_dir():
        print(f"Error: {source_dir} is not a directory", file=sys.stderr)
        return 1

    clean_output(output_root)
    mmd_dir.mkdir(parents=True, exist_ok=True)

    if args.target_dir:
        target_path = resolve_target_dir(source_dir, args.target_dir)

        if not target_path.is_dir():
            print(f"Error: {target_path} is not a directory", file=sys.stderr)
            return 1

        print(f"Scanning for Java files in {target_path} ...")
        java_files = sorted(find_java_files(str(target_path)))
        print(f"Found {len(java_files)} .java files.")

        if not java_files:
            print("No Java files found. Nothing to generate.", file=sys.stderr)
            return 1

        entities = []
        for file_path in java_files:
            tree = parse_java_file(file_path)
            entities.extend(extract_info(tree, file_path))

        print(f"Extracted {len(entities)} classes/interfaces/enums.")

        if not entities:
            print(
                "No classes/interfaces/enums extracted. Nothing to generate.",
                file=sys.stderr,
            )
            return 1

        known, simple_to_full = build_knowledge(entities)
        graph = build_graph(entities, known, simple_to_full)
        root_package = (
            entities[0]["package"]
            if entities
            else str(target_path.relative_to(source_dir)).replace("/", ".")
        )
        selected_nodes = set(known.keys())
        content = generate_leaf_mermaid(
            root_package,
            selected_nodes,
            known,
            graph,
            args.fields,
            args.methods,
            root_package=root_package,
        )

        output_base = (
            args.output_file
            if args.output_file
            else f"package_{sanitize_package_filename(root_package)}"
        )
        file_path = mmd_dir / f"{output_base}.mmd"
        file_path.write_text(content, encoding="utf-8")
        print(f"Generated 1 Mermaid file in {mmd_dir}.")
        return 0

    print(f"Scanning for Java files in {source_dir} ...")
    java_files = sorted(find_java_files(str(source_dir)))
    print(f"Found {len(java_files)} .java files.")

    entities = []
    for file_path in java_files:
        tree = parse_java_file(file_path)
        entities.extend(extract_info(tree, file_path))

    print(f"Extracted {len(entities)} classes/interfaces/enums.")
    known, simple_to_full = build_knowledge(entities)
    graph = build_graph(entities, known, simple_to_full)
    package_entities, package_children, packages = build_package_maps(entities)
    subtree_cache = build_subtree_cache(packages, package_entities, package_children)
    diagram_plan = plan_package_diagrams(
        packages, package_entities, package_children, subtree_cache, args.threshold
    )

    mmd_files = []
    for package_name, diagram_kind, selected_nodes, child_packages in diagram_plan:
        if diagram_kind == "leaf":
            content = generate_leaf_mermaid(
                package_name,
                selected_nodes,
                known,
                graph,
                args.fields,
                args.methods,
                root_package=package_name,
            )
        else:
            content = generate_branch_mermaid(
                package_name,
                selected_nodes,
                child_packages,
                known,
                graph,
                args.fields,
                args.methods,
                subtree_cache,
            )
        file_path = mmd_dir / f"package_{sanitize_package_filename(package_name)}.mmd"
        file_path.write_text(content, encoding="utf-8")
        mmd_files.append(file_path)

    print(f"Generated {len(mmd_files)} Mermaid file(s) in {mmd_dir}.")
    return 0


def build_parser():
    script_dir = Path(__file__).resolve().parent
    default_output = script_dir / "generated"
    default_source = script_dir.parent / "src"

    parser = argparse.ArgumentParser(
        description="Generate package-aware Mermaid UML files.",
        epilog=(
            "Examples:\n"
            "  python3 uml/generate_src_mermaid.py\n"
            "  python3 uml/generate_src_mermaid.py --target-dir src/game/java/edu/nust/game/systems\n"
            "  python3 uml/generate_src_mermaid.py --source-dir src --output-dir uml/generated"
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
        help="Directory for generated .mmd files.",
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
        "--target-dir",
        type=str,
        help="Generate diagram for this specific directory only (path relative to source-dir or absolute).",
    )
    parser.add_argument(
        "--output-file",
        type=str,
        default=None,
        help="Base name for output file when using --target-dir (without extension).",
    )
    return parser


def parse_args(argv=None):
    return build_parser().parse_args(argv)


def main(argv=None):
    return write_mermaid_files(parse_args(argv))


if __name__ == "__main__":
    raise SystemExit(main())