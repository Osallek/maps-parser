package fr.osallek.mapsparser.service.object;

public class Hashed {

    protected int initHash;

    public void initHash() {
        this.initHash = hashCode();
    }

    public int getInitHash() {
        return initHash;
    }
}
