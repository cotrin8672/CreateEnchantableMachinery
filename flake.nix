{
  description = "Minecraft mod dev shell";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-25.11";
  };

  outputs = {
    self,
    nixpkgs,
  }: let
    system = "x86_64-linux";
    pkgs = import nixpkgs {inherit system;};
    jdk = pkgs.jdk21;
  in {
    devShells.${system}.default = pkgs.mkShell {
      packages = with pkgs; [
        jdk
        gradle
        git
        unzip
      ];

      JAVA_HOME = "${jdk}/lib/openjdk";

      shellHook = ''
        export PATH="$JAVA_HOME/bin:$PATH"
      '';
    };
  };
}
