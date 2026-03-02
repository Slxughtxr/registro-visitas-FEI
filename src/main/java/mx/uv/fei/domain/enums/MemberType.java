package mx.uv.fei.domain.enums;

public enum MemberType {
    STUDENT("Alumno"),
    ACADEMIC("Académico"),
    ADMINISTRATIVE("Administrativo");

    private final String databaseValue;

    MemberType(String databaseValue) {
        this.databaseValue = databaseValue;
    }

    public String getDatabaseValue() {
        return databaseValue;
    }
}