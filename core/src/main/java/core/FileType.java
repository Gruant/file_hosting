package core;

public enum FileType {
    FILE("File"), DIRECTORY("DIR");

    private String name;

    public String getName() {
        return name;
    }

    FileType(String name) {
        this.name = name;
    }
}

