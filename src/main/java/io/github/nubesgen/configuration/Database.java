package io.github.nubesgen.configuration;

public class Database {

    private DatabaseType type;

    private Size size;

    public Database() {
        this.type = DatabaseType.MYSQL;
        this.size = Size.S;
    }

    public Database(DatabaseType type, Size size) {
        this.type = type;
        this.size = size;
    }

    public DatabaseType getType() {
        return type;
    }

    public void setType(DatabaseType type) {
        this.type = type;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "Database{" +
                "type=" + type +
                ", size=" + size +
                '}';
    }
}
