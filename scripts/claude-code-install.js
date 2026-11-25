// Registers the FRC AI Copilot MCP server with Claude Code, by writing `.mcp.json` at the project
// root. Shared build/JDK/launcher logic lives in ./mcp-install.js.
//
// Unlike the other editors this writes into the project rather than the home directory, because
// that is where Claude Code looks for a project-scoped server. `.mcp.json` is gitignored: the paths
// in it are absolute, so a committed copy points at nothing on any other machine.
//
// Worth running rather than hand-writing the file, on Windows especially. The launcher there is a
// `.bat`, and a client that spawns it without a shell — which Claude Code does — cannot start it
// directly; the entry has to be `cmd.exe /c <launcher.bat>`. That is the same bug the editor
// installers had, and it is already solved in the shared module, so this reuses it rather than
// restating it.

const path = require('path');
const { buildServer, projectRoot, registerJson } = require('./mcp-install');

const { executablePath, javaHome } = buildServer();
const configPath = path.join(projectRoot(), '.mcp.json');
registerJson(configPath, executablePath, javaHome);

console.log(`\nWrote ${configPath}`);
console.log('Restart Claude Code and approve the project MCP server to see the tools.');
