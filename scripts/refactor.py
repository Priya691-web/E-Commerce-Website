import os
import glob
import re

dao_dir = r"c:\Users\DELL\Desktop\FashionStore\src\main\java\com\fashionstore\dao\implementation"
files = glob.glob(os.path.join(dao_dir, "*.java"))

for filepath in files:
    with open(filepath, 'r') as f:
        content = f.read()

    # Pattern to match: try (Connection conn = DBConnection.getConnection(); Statement stmt = ...) {
    # We will replace it with:
    # try (Connection conn = DBConnection.getConnection()) {
    #     if (conn == null) throw new SQLException("Database connection is null");
    #     try (Statement stmt = ...) {
    
    # We need to handle multiple resources separated by ;
    def replacer(match):
        full_match = match.group(0)
        # full_match is something like:
        # try (Connection conn = DBConnection.getConnection();
        #      PreparedStatement ps = conn.prepareStatement(query)) {
        
        # Split the resources part
        inside_parens = match.group(1)
        
        # Find the first semicolon which separates Connection from the rest
        first_semi = inside_parens.find(';')
        if first_semi == -1:
            return full_match # Should not happen if it matches pattern
        
        conn_part = inside_parens[:first_semi].strip()
        rest_part = inside_parens[first_semi+1:].strip()
        
        indent = match.group(0)[:match.group(0).find('try')]
        
        replacement = f"""try ({conn_part}) {{
{indent}    if (conn == null) {{
{indent}        throw new SQLException("Database connection is null");
{indent}    }}
{indent}    try ({rest_part}) {{"""
        return replacement

    # Pattern: try ( [whitespaces] Connection conn = DBConnection.getConnection(); [any] ) {
    new_content = re.sub(r'([ \t]*)try\s*\(\s*Connection\s+conn\s*=\s*DBConnection\.getConnection\(\)\s*;(.*?)\)\s*\{', replacer, content, flags=re.DOTALL)
    
    # Now we need to add an extra closing brace } for every replacement made, before the catch block
    # This is tricky with regex. Let's do it by finding catch (SQLException e) and prepending }
    
    # Instead of regex for the closing brace, we can do it by counting or just doing a simpler replacement
    # A simpler way: The old code had:
    #        } catch (SQLException e) {
    # We replaced `try (...) {` with `try (...) { ... try (...) {`. So we need `} } catch (SQLException e) {`.
    # Let's count how many replacements were made in this file
    num_replacements = len(re.findall(r'try\s*\(\s*Connection\s+conn\s*=\s*DBConnection\.getConnection\(\)\s*;', content))
    if num_replacements > 0:
        new_content = re.sub(r'([ \t]*)\}\s*catch\s*\(\s*SQLException\s+e\s*\)\s*\{', r'\1}\n\1} catch (SQLException e) {', new_content)
        with open(filepath, 'w') as f:
            f.write(new_content)
        print(f"Refactored {filepath} with {num_replacements} replacements.")

