package io.oliverj.mammon.banking;

public interface ISignable {
    byte[] toByteArray();
    byte[] toFullByteArray();
}
