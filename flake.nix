{
  description = "BranchLinkSimulatorAndroid flake";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-26.05";
    utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, utils }:
    utils.lib.eachDefaultSystem (system:
      let
        pkgs = nixpkgs.legacyPackages.${system};
        jdk = pkgs.openjdk21;
      in
      {
        devShells.default = pkgs.mkShell {
          packages = [
            jdk
          ];

          JAVA_HOME = "${jdk}";
        };

        formatter = pkgs.nixfmt-rfc-style;
      }
    );
}