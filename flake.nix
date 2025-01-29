{
  description = "BranchLinkSimulatorAndroid flake";
  inputs = {
    branch-nix.url = "git+ssh://git@github.com/BranchMetrics/nix";
  };

  outputs = { self, branch-nix }:
    let
      nixpkgs = branch-nix.nixpkgs;
      extra-pkgs = branch-nix.packages;
      utils = branch-nix.utils;
      system-utils = branch-nix.system-utils;
    in
    utils.lib.eachDefaultSystem (system:
      let
        pkgs = nixpkgs.legacyPackages.${system} // {
          extra-pkgs = extra-pkgs.${system};
        };
        jdk = pkgs.openjdk21;
      in
      {
        devShell = pkgs.mkShell {
          nativeBuildInputs = [
            jdk
          ];
          JAVA_HOME = "${jdk}";
        };
        formatter = pkgs.nixpkgs-fmt;
      }
    );
}