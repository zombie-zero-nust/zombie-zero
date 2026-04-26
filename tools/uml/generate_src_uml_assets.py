#!/usr/bin/env python3
# AI USE DISCLOSURE
"""Generate Mermaid UML diagrams and rendered assets for the src tree."""

import argparse
import json
import os
import re
import shutil
import subprocess
import sys
import tempfile
from collections import defaultdict, deque
from pathlib import Path

import javalang
from javalang.tree import ClassDeclaration, EnumDeclaration, InterfaceDeclaration

RECORD_PATTERN = re.compile(
    r'(?P<prefix>(?:public|protected|private|abstract|static|final|strictfp|sealed|non-sealed)\s+)*'
    r'record\s+(?P<name>\w+)\s*\((?P<components>[^)]*)\)'
    r'(?P<suffix>\s*(?:implements\s+[^{]+)?)\s*\{',
    re.MULTILINE,
)
CONTROL_KEYWORDS = {'if', 'for', 'while', 'switch', 'catch', 'synchronized', 'try', 'else', 'do'}
TYPE_KEYWORDS = {'class', 'interface', 'enum'}
SUPPORTED_SUFFIXES = {'.mmd', '.mermaid'}


def find_java_files(root_dir):
    for dirpath, _, filenames in os.walk(root_dir):
        for fname in filenames:
            if fname.endswith('.java'):
                yield os.path.join(dirpath, fname)


def _convert_record(match):
    prefix = match.group('prefix') or ''
    prefix = re.sub(r'\b(?:sealed|non-sealed)\b\s*', '', prefix)
    name = match.group('name')
    components = match.group('components').strip()
    suffix = match.group('suffix') or ''

    fields = []
    if components:
        for component in components.split(','):
            component = component.strip()
            if not component:
                continue
            field_type, field_name = component.rsplit(' ', 1)
            fields.append(f'    private {field_type} {field_name};')

    body = '\n'.join(fields)
    if body:
        body = '\n' + body + '\n'
    return f'{prefix}class {name}{suffix} {{{body}'


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
    last_word = ''

    while index < length:
        char = source[index]
        next_char = source[index + 1] if index + 1 < length else ''

        if line_comment:
            result.append(char)
            if char == '\n':
                line_comment = False
            index += 1
            continue

        if block_comment:
            result.append(char)
            if char == '*' and next_char == '/':
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
            elif char == '\\':
                escape = True
            elif char == string_delim:
                string_delim = None
            index += 1
            continue

        if char == '/' and next_char == '/':
            result.extend([char, next_char])
            index += 2
            line_comment = True
            continue

        if char == '/' and next_char == '*':
            result.extend([char, next_char])
            index += 2
            block_comment = True
            continue

        if char in {'"', "'"}:
            result.append(char)
            string_delim = char
            index += 1
            continue

        if char == '@':
            index += 1
            while index < length and (source[index].isalnum() or source[index] in {'_', '.', '$'}):
                index += 1
            if index < length and source[index] == '(':
                depth = 1
                index += 1
                while index < length and depth > 0:
                    if source[index] == '(':
                        depth += 1
                    elif source[index] == ')':
                        depth -= 1
                    elif source[index] in {'"', "'"}:
                        quote = source[index]
                        index += 1
                        while index < length:
                            if source[index] == '\\':
                                index += 2
                                continue
                            if source[index] == quote:
                                break
                            index += 1
                    index += 1
            while index < length and source[index].isspace() and source[index] != '\n':
                index += 1
            continue

        if char.isalpha() or char == '_' or (last_word and char.isdigit()):
            start = index
            while index < length and (source[index].isalnum() or source[index] in {'_', '$', '-'}):
                index += 1
            word = source[start:index]
            result.append(word)
            last_word = word

            if word in TYPE_KEYWORDS or word == 'record':
                pending_header = False
                header_token = word
                seen_paren_in_header = False
            elif paren_depth == 0 and angle_depth == 0 and word not in CONTROL_KEYWORDS:
                pending_header = True
                header_token = word
            continue

        if char == '<' and not pending_header:
            angle_depth += 1
        elif char == '>' and angle_depth > 0 and not pending_header:
            angle_depth -= 1
        elif char == '(':
            paren_depth += 1
            if pending_header:
                seen_paren_in_header = True
        elif char == ')':
            paren_depth -= 1
        elif char == ';':
            pending_header = False
            seen_paren_in_header = False
            header_token = None
        elif char == '{':
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
                    next_inner = source[index + 1] if index + 1 < length else ''
                    if current == '/' and next_inner == '/':
                        index += 2
                        while index < length and source[index] != '\n':
                            index += 1
                        continue
                    if current == '/' and next_inner == '*':
                        index += 2
                        while index + 1 < length and not (source[index] == '*' and source[index + 1] == '/'):
                            index += 1
                        index += 2
                        continue
                    if current in {'"', "'"}:
                        quote = current
                        index += 1
                        while index < length:
                            if source[index] == '\\':
                                index += 2
                                continue
                            if source[index] == quote:
                                index += 1
                                break
                            index += 1
                        continue
                    if current == '{':
                        depth += 1
                    elif current == '}':
                        depth -= 1
                    index += 1
                result.append('}')
                pending_header = False
                seen_paren_in_header = False
                header_token = None
                continue

            pending_header = False
            seen_paren_in_header = False
            header_token = None
            last_word = ''
            continue
        elif char == '}':
            pending_header = False
            seen_paren_in_header = False
            header_token = None

        result.append(char)
        index += 1

    return ''.join(result)


def sanitize_java_source(source):
    source = source.replace('\n///', '\n//')
    if source.startswith('///'):
        source = '//' + source[3:]

    source = re.sub(r'\bnon-sealed\b\s*', '', source)
    source = re.sub(r'\bsealed\b\s+', '', source)
    source = re.sub(r'\s+permits\s+[^{]+(?=\{)', '', source)
    source = re.sub(
        r'\binstanceof\s+([A-Z][\w$.<>?, ]+)\s+[a-zA-Z_$][\w$]*',
        r'instanceof \1',
        source,
    )
    source = RECORD_PATTERN.sub(_convert_record, source)
    source = re.sub(r'\b(public|protected|private)\s+([A-Z][\w$]*)\s*\{', r'\1 \2() {', source)
    return strip_method_bodies(source)


def parse_java_file(filepath):
    try:
        with open(filepath, 'r', encoding='utf-8') as handle:
            source = sanitize_java_source(handle.read())
            return javalang.parse.parse(source)
    except Exception as exc:
        description = getattr(exc, 'description', str(exc)) or type(exc).__name__
        location = getattr(exc, 'at', None)
        if location is not None:
            print(f'  [WARN] Could not parse {filepath}: {description} at {location}')
        else:
            print(f'  [WARN] Could not parse {filepath}: {description}')
        return None


def get_full_name(package, name):
    return f'{package}.{name}' if package else name


def resolve_type(ref_type, imports, current_package):
    if ref_type is None:
        return None
    if isinstance(ref_type, javalang.tree.BasicType):
        return ref_type.name
    if isinstance(ref_type, javalang.tree.ReferenceType):
        base = ref_type.name
        if '.' in base:
            return base
        for imp in imports:
            if imp.endswith('.' + base) or imp.endswith('.' + base + '.*'):
                if imp.endswith('.*'):
                    return imp[:-2] + '.' + base
                return imp
        if current_package:
            return f'{current_package}.{base}'
        return base
    return None


def extract_info(tree, filepath):
    if tree is None:
        return []
    package = tree.package.name if tree.package else ''
    imports = [imp.path for imp in tree.imports] if tree.imports else []
    entities = []
    for _, node in tree.filter(javalang.tree.TypeDeclaration):
        if not isinstance(node, (ClassDeclaration, InterfaceDeclaration, EnumDeclaration)):
            continue
        name = node.name
        full_name = get_full_name(package, name)
        kind = type(node).__name__.replace('Declaration', '')
        super_class = None
        interfaces = []
        if isinstance(node, ClassDeclaration):
            super_class = node.extends.name if node.extends else None
            interfaces = [interface.name for interface in node.implements] if node.implements else []
        elif isinstance(node, InterfaceDeclaration):
            interfaces = [interface.name for interface in node.extends] if node.extends else []

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
                    params.append((param.name, resolve_type(param.type, imports, package)))
                return_type = resolve_type(member.return_type, imports, package) if member.return_type else 'void'
                methods.append((member.name, return_type, params))

        entities.append({
            'full_name': full_name,
            'name': name,
            'kind': kind,
            'super': super_class,
            'interfaces': interfaces,
            'fields': fields,
            'methods': methods,
            'imports': imports,
            'package': package,
            'file': filepath,
        })
    return entities


def build_knowledge(entities):
    known = {}
    simple_to_full = defaultdict(list)
    for entity in entities:
        known[entity['full_name']] = entity
        simple_to_full[entity['name']].append(entity['full_name'])
    return known, simple_to_full


def resolve_full_class(name, entity, known, simple_to_full):
    if name is None:
        return None
    if name in known:
        return name
    for imp in entity['imports']:
        if imp.endswith('.' + name) and imp in known:
            return imp
        if imp.endswith('.*'):
            candidate = f'{imp[:-2]}.{name}'
            if candidate in known:
                return candidate
    if entity['package']:
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
        src = entity['full_name']
        if entity['super']:
            sup = resolve_full_class(entity['super'], entity, known, simple_to_full)
            if sup and sup in all_nodes:
                graph[src].add(sup)
        for interface in entity['interfaces']:
            iface_full = resolve_full_class(interface, entity, known, simple_to_full)
            if iface_full and iface_full in all_nodes:
                graph[src].add(iface_full)
        for _, field_type in entity['fields']:
            if field_type:
                resolved = resolve_full_class(field_type, entity, known, simple_to_full)
                if resolved and resolved in all_nodes and resolved != src:
                    graph[src].add(resolved)
        for _, return_type, params in entity['methods']:
            if return_type and return_type != 'void':
                resolved = resolve_full_class(return_type, entity, known, simple_to_full)
                if resolved and resolved in all_nodes and resolved != src:
                    graph[src].add(resolved)
            for _, param_type in params:
                if param_type:
                    resolved = resolve_full_class(param_type, entity, known, simple_to_full)
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
        if len(simple_to_full[entity['name']]) == 1:
            base_alias = entity['name']
        else:
            base_alias = re.sub(r'\W+', '_', full_name)
        alias = base_alias
        counter = 2
        while alias in used_aliases and used_aliases[alias] != full_name:
            alias = f'{base_alias}_{counter}'
            counter += 1
        used_aliases[alias] = full_name
        alias_map[full_name] = alias
    return alias_map


def sanitize_package_filename(package_name):
    return re.sub(r'\W+', '_', package_name)


def build_class_declaration(entity, alias, include_fields, include_methods):
    stereotype = ''
    if entity['kind'] == 'Interface':
        stereotype = ' <<interface>>'
    elif entity['kind'] == 'Enum':
        stereotype = ' <<enumeration>>'

    decl = f'class {alias}{stereotype}'
    if include_fields or include_methods:
        decl += ' {'
        members = []
        if include_fields:
            for field_name, field_type in entity['fields']:
                short_type = field_type.split('.')[-1] if field_type else 'Object'
                members.append(f'  +{short_type} {field_name}')
        if include_methods:
            for method_name, return_type, params in entity['methods']:
                param_str = ', '.join(
                    f"{param_type.split('.')[-1] if param_type else 'Object'} {param_name}"
                    for param_name, param_type in params
                )
                short_return = return_type.split('.')[-1] if return_type and return_type != 'void' else 'void'
                members.append(f'  +{short_return} {method_name}({param_str})')
        decl += '\n' + '\n'.join(members) + '\n}'
    return decl


def build_subset_simple_to_full(selected_nodes, known):
    simple_to_full = defaultdict(list)
    for full_name in selected_nodes:
        simple_to_full[known[full_name]['name']].append(full_name)
    return simple_to_full


def topological_sort_subset(graph, selected_nodes):
    subset_graph = {
        node: {target for target in graph.get(node, set()) if target in selected_nodes}
        for node in selected_nodes
        if node in graph or any(node in targets for targets in graph.values())
    }
    sorted_nodes = [node for node in topological_sort(subset_graph) if node in selected_nodes]
    remainder = sorted(node for node in selected_nodes if node not in sorted_nodes)
    return sorted_nodes + remainder


def mermaid_header(title):
    return [
        '---',
        f'title: {title}',
        'config:',
        '  look: classic',
        '  layout: dagre',
        '  flowchart:',
        '    curve: linear',
        '  class:',
        '    defaultRenderer: dagre-wrapper',
        '---',
        'classDiagram',
    ]


def generate_leaf_mermaid(package_name, selected_nodes, known, graph, include_fields, include_methods):
    if not selected_nodes:
        return '\n'.join(mermaid_header(package_name) + ['%% No classes in selection'])

    selected_entities = {name: known[name] for name in selected_nodes}
    simple_to_full = build_subset_simple_to_full(selected_nodes, known)
    alias_map = build_mermaid_name_maps(selected_entities, simple_to_full)
    lines = mermaid_header(package_name)

    for full_name in topological_sort_subset(graph, selected_nodes):
        lines.append(build_class_declaration(known[full_name], alias_map[full_name], include_fields, include_methods))

    for src in sorted(selected_nodes):
        for tgt in sorted(target for target in graph.get(src, set()) if target in selected_nodes):
            src_entity = known[src]
            tgt_entity = known[tgt]
            src_alias = alias_map[src]
            tgt_alias = alias_map[tgt]
            if tgt_entity['kind'] == 'Interface':
                resolved_interfaces = [
                    resolve_full_class(interface, src_entity, selected_entities, simple_to_full)
                    for interface in src_entity['interfaces']
                ]
                if tgt in resolved_interfaces:
                    lines.append(f'{src_alias} ..|> {tgt_alias} : implements')
                else:
                    lines.append(f'{src_alias} ..> {tgt_alias} : uses')
            else:
                super_full = resolve_full_class(src_entity.get('super'), src_entity, selected_entities, simple_to_full)
                if super_full == tgt:
                    lines.append(f'{tgt_alias} <|-- {src_alias} : extends')
                else:
                    lines.append(f'{src_alias} --> {tgt_alias} : uses')

    return '\n'.join(lines)


def build_package_maps(entities):
    package_entities = defaultdict(set)
    package_children = defaultdict(set)
    packages = set()
    for entity in entities:
        package = entity['package']
        package_entities[package].add(entity['full_name'])
        segments = package.split('.')
        for index in range(len(segments)):
            current = '.'.join(segments[: index + 1])
            packages.add(current)
            if index > 0:
                parent = '.'.join(segments[:index])
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

    for package in sorted(packages, key=lambda item: item.count('.'), reverse=True):
        collect(package)
    return subtree_cache


def package_id(package_name):
    return f'Package_{sanitize_package_filename(package_name)}'


def package_stereotype(package_name, subtree_nodes):
    return f'class {package_id(package_name)} <<package>>'


def summarize_branch_edges(package_name, direct_classes, child_packages, subtree_cache, graph):
    lines = []
    direct_class_set = set(direct_classes)

    for src in sorted(direct_class_set):
        for tgt in sorted(target for target in graph.get(src, set()) if target in direct_class_set):
            lines.append((src, tgt, 'class'))

    for child in sorted(child_packages):
        child_nodes = subtree_cache[child]
        has_outbound = any(target in child_nodes for src in direct_class_set for target in graph.get(src, set()))
        if has_outbound:
            for src in sorted(direct_class_set):
                if any(target in child_nodes for target in graph.get(src, set())):
                    lines.append((src, child, 'class_to_package'))

        for dst in sorted(direct_class_set):
            if any(dst in graph.get(src, set()) for src in child_nodes):
                lines.append((child, dst, 'package_to_class'))

    for child_src in sorted(child_packages):
        source_nodes = subtree_cache[child_src]
        for child_tgt in sorted(pkg for pkg in child_packages if pkg != child_src):
            target_nodes = subtree_cache[child_tgt]
            if any(target in target_nodes for src in source_nodes for target in graph.get(src, set())):
                lines.append((child_src, child_tgt, 'package_to_package'))

    return lines


def generate_branch_mermaid(package_name, direct_classes, child_packages, known, graph, include_fields, include_methods, subtree_cache):
    selected_entities = {name: known[name] for name in direct_classes}
    simple_to_full = build_subset_simple_to_full(direct_classes, known)
    alias_map = build_mermaid_name_maps(selected_entities, simple_to_full) if direct_classes else {}
    lines = mermaid_header(package_name)

    for full_name in topological_sort_subset(graph, direct_classes):
        lines.append(build_class_declaration(known[full_name], alias_map[full_name], include_fields, include_methods))

    for child in sorted(child_packages):
        lines.append(package_stereotype(child, subtree_cache[child]))

    for source, target, edge_kind in summarize_branch_edges(package_name, direct_classes, child_packages, subtree_cache, graph):
        if edge_kind == 'class':
            lines.append(f'{alias_map[source]} --> {alias_map[target]} : uses')
        elif edge_kind == 'class_to_package':
            lines.append(f'{alias_map[source]} --> {package_id(target)} : uses')
        elif edge_kind == 'package_to_class':
            lines.append(f'{package_id(source)} --> {alias_map[target]} : uses')
        else:
            lines.append(f'{package_id(source)} --> {package_id(target)} : uses')

    if len(lines) == len(mermaid_header(package_name)):
        lines.append('%% No classes or child packages to show')

    return '\n'.join(lines)


def plan_package_diagrams(packages, package_entities, package_children, subtree_cache, threshold):
    diagrams = []

    def visit(package_name):
        subtree_nodes = subtree_cache.get(package_name, set())
        if not subtree_nodes:
            return
        if len(subtree_nodes) <= threshold:
            diagrams.append((package_name, 'leaf', set(subtree_nodes), set()))
            return
        diagrams.append((
            package_name,
            'branch',
            set(package_entities.get(package_name, set())),
            set(package_children.get(package_name, set())),
        ))
        for child in sorted(package_children.get(package_name, set())):
            visit(child)

    top_level_packages = sorted(package for package in packages if '.' not in package)
    for package in top_level_packages:
        visit(package)
    return diagrams


def find_mermaid_cli():
    resolved = shutil.which('mmdc')
    if resolved:
        return [resolved]
    if shutil.which('npx'):
        return ['npx', '-y', '@mermaid-js/mermaid-cli']
    raise RuntimeError('Mermaid CLI is not available. Install mmdc or ensure npx is on PATH.')


def create_mermaid_config(max_text_size, max_edges):
    config = {
        'look': 'classic',
        'layout': 'dagre',
        'flowchart': {'curve': 'linear'},
        'class': {'defaultRenderer': 'dagre-wrapper'},
        'maxTextSize': max_text_size,
        'maxEdges': max_edges,
    }
    handle = tempfile.NamedTemporaryFile('w', encoding='utf-8', suffix='.json', delete=False)
    with handle:
        json.dump(config, handle)
    return Path(handle.name)


def build_render_command(cli_command, source_file, output_file, args, config_file):
    command = [*cli_command, '-i', str(source_file), '-o', str(output_file), '-c', str(config_file)]
    if args.theme:
        command.extend(['-t', args.theme])
    if args.scale is not None:
        command.extend(['-s', str(args.scale)])
    if args.width is not None:
        command.extend(['-w', str(args.width)])
    if args.height is not None:
        command.extend(['-H', str(args.height)])
    if output_file.suffix.lower() == '.png':
        command.extend(['-b', args.background_color])
    return command


def render_diagrams(mmd_files, output_root, args):
    cli_command = find_mermaid_cli()
    pdf_dir = output_root / 'pdf'
    png_dir = output_root / 'png'
    pdf_dir.mkdir(parents=True, exist_ok=True)
    png_dir.mkdir(parents=True, exist_ok=True)
    config_file = create_mermaid_config(args.max_text_size, args.max_edges)
    rendered = []
    try:
        for mmd_file in mmd_files:
            for suffix, destination_dir in (('pdf', pdf_dir), ('png', png_dir)):
                output_file = destination_dir / f'{mmd_file.stem}.{suffix}'
                command = build_render_command(cli_command, mmd_file, output_file, args, config_file)
                result = subprocess.run(command, capture_output=True, text=True)
                if result.returncode != 0:
                    stderr = result.stderr.strip() or result.stdout.strip() or 'Unknown error'
                    raise RuntimeError(f'Failed to render {mmd_file.name} to {suffix}: {stderr}')
                rendered.append(output_file)
    finally:
        config_file.unlink(missing_ok=True)
    return rendered


def clean_output(output_root):
    if output_root.exists():
        shutil.rmtree(output_root)
    output_root.mkdir(parents=True, exist_ok=True)


def parse_args(argv=None):
    script_dir = Path(__file__).resolve().parent
    default_output = script_dir / 'generated'
    default_source = script_dir.parent.parent / 'src'

    parser = argparse.ArgumentParser(description='Generate package-aware Mermaid UML and render PDF/PNG assets.')
    parser.add_argument('--source-dir', type=Path, default=default_source, help='Source tree to scan for Java files.')
    parser.add_argument('--output-dir', type=Path, default=default_output, help='Directory for generated .mmd/.pdf/.png files.')
    parser.add_argument('--threshold', type=int, default=24, help='Maximum classes per leaf diagram before splitting deeper.')
    parser.add_argument('--fields', action='store_true', help='Include fields in class diagrams.')
    parser.add_argument('--methods', action='store_true', help='Include methods in class diagrams.')
    parser.add_argument('--skip-render', action='store_true', help='Generate .mmd files only.')
    parser.add_argument('--theme', help='Optional Mermaid theme for rendering.')
    parser.add_argument('--background-color', default='white', help='PNG background color.')
    parser.add_argument('--scale', type=float, help='Optional Mermaid CLI scale.')
    parser.add_argument('--width', type=int, help='Optional Mermaid CLI width.')
    parser.add_argument('--height', type=int, help='Optional Mermaid CLI height.')
    parser.add_argument('--max-text-size', type=int, default=1_000_000, help='Mermaid maxTextSize override.')
    parser.add_argument('--max-edges', type=int, default=100_000, help='Mermaid maxEdges override.')
    return parser.parse_args(argv)


def main(argv=None):
    args = parse_args(argv)
    source_dir = args.source_dir.resolve()
    output_root = args.output_dir.resolve()
    mmd_dir = output_root / 'mmd'

    if not source_dir.is_dir():
        print(f'Error: {source_dir} is not a directory', file=sys.stderr)
        return 1

    print(f'Scanning for Java files in {source_dir} ...')
    java_files = sorted(find_java_files(str(source_dir)))
    print(f'Found {len(java_files)} .java files.')

    entities = []
    for file_path in java_files:
        tree = parse_java_file(file_path)
        entities.extend(extract_info(tree, file_path))

    print(f'Extracted {len(entities)} classes/interfaces/enums.')
    known, simple_to_full = build_knowledge(entities)
    graph = build_graph(entities, known, simple_to_full)
    package_entities, package_children, packages = build_package_maps(entities)
    subtree_cache = build_subtree_cache(packages, package_entities, package_children)
    diagram_plan = plan_package_diagrams(packages, package_entities, package_children, subtree_cache, args.threshold)

    clean_output(output_root)
    mmd_dir.mkdir(parents=True, exist_ok=True)

    mmd_files = []
    for package_name, diagram_kind, selected_nodes, child_packages in diagram_plan:
        if diagram_kind == 'leaf':
            content = generate_leaf_mermaid(package_name, selected_nodes, known, graph, args.fields, args.methods)
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
        file_path = mmd_dir / f'package_{sanitize_package_filename(package_name)}.mmd'
        file_path.write_text(content, encoding='utf-8')
        mmd_files.append(file_path)

    print(f'Generated {len(mmd_files)} Mermaid file(s) in {mmd_dir}.')

    if args.skip_render:
        return 0

    try:
        rendered_files = render_diagrams(mmd_files, output_root, args)
    except Exception as exc:
        print(f'Error: {exc}', file=sys.stderr)
        return 1

    print(f'Rendered {len(rendered_files)} asset file(s) in {output_root}.')
    return 0


if __name__ == '__main__':
    raise SystemExit(main())