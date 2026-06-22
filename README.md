
# SIMULADOR DE SISTEMA SOLAR

### PROJETO PARA AVALIAÇÃO 2 DA MATÉRIA PROGRAMAÇÃO ORIENTADA À OBJETOS II
##### 2026.01

### RODAR NO LINUX (DEBIAN)

```bash
find src -name "*.class" -delete
rm -rf bin
mkdir -p bin
javac -d bin $(find src -name "*.java")
java -cp bin app.Main
```

### PRÉ-REQUISITOS PARA RODAR NO WINDOWS

- Ter o JDK instalado.
- Ter os comandos `java` e `javac` disponíveis no terminal.

Para verificar:
```bat
java -version
javac -version
```

##### Pelo Prompt de Comando (CMD)

Na raiz do projeto:

```bat
rmdir /s /q bin
mkdir bin
for /r src %f in (*.java) do @echo %f >> sources.txt
javac -d bin @sources.txt
del sources.txt
java -cp bin app.Main
```

##### Pelo PowerShell

Na raiz do projeto:

```powershell
Remove-Item -Recurse -Force bin -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Path bin | Out-Null
Get-ChildItem -Recurse -Path src -Filter *.java | ForEach-Object { $_.FullName } | Set-Content sources.txt
javac -d bin @sources.txt
Remove-Item sources.txt
java -cp bin app.Main
```

### FUNCIONALIDADES

- Simulação de planetas e satélites
- Órbitas elípticas
- Foguetes com destino configurável
- Asteroides com movimento livre
- Tooltip com informações dos corpos
- Painel HUD com dados da simulação
- Controle visual de estrelas

### ESTRUTURA DO PROJETO
