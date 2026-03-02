package mx.uv.fei.domain.entities;

import mx.uv.fei.domain.enums.MemberType;

public class InstitutionalMember extends Visitor {
    private String institutionalId;
    private MemberType memberType;

    public InstitutionalMember() {
        super();
    }

    public InstitutionalMember(String firstName, String lastName, String email, String institutionalId, MemberType memberType) {
        super(firstName, lastName, email);
        this.institutionalId = institutionalId;
        this.memberType = memberType;
    }

    public String getInstitutionalId() {
        return institutionalId;
    }

    public void setInstitutionalId(String institutionalId) {
        this.institutionalId = institutionalId;
    }

    public MemberType getMemberType() {
        return memberType;
    }

    public void setMemberType(MemberType memberType) {
        this.memberType = memberType;
    }
}