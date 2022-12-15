{ pkgs ? import (fetchTarball "https://github.com/NixOS/nixpkgs/archive/6bc6f77cb171a74001033d94f17f49043a9f1804.tar.gz") {}
}:let

in pkgs.mkShell {

  buildInputs = with pkgs; [
    git
    jdk19_headless
    maven
  ];

}
