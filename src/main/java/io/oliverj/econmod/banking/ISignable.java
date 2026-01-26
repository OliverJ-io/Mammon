package io.oliverj.econmod.banking;

public interface ISignable {
    byte[] toByteArray();
    byte[] toFullByteArray();
}
