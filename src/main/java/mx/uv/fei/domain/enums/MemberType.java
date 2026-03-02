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

    public static MemberType fromDatabaseValue(String value) {
        for (MemberType type : MemberType.values()) {
            if (type.getDatabaseValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("Tipo de miembro desconocido en la BD: " + value);
    }
}