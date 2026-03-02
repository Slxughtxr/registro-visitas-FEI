package mx.uv.fei.infrastructure.repositories;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import mx.uv.fei.domain.entities.InstitutionalMember;
import mx.uv.fei.domain.enums.MemberType;
import mx.uv.fei.infrastructure.exceptions.DAOException;

public class InstitutionalMemberDAOTest {

    @Test
    public void testInsertInstitutionalMemberSuccess() throws DAOException {
        InstitutionalMemberDAO dao = new InstitutionalMemberDAO();
        InstitutionalMember student = new InstitutionalMember(
            "Angel Gabriel", 
            "Aguilar Hernández", 
            "angel@uv.mx", 
            "S24013324", 
            MemberType.STUDENT
        );

        boolean isSaved = dao.insertInstitutionalMember(student);

        assertTrue(isSaved, "El registro del estudiante debería insertarse correctamente en la BD");
    }
}