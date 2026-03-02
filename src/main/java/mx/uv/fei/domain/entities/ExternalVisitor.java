package mx.uv.fei.domain.entities;

public class ExternalVisitor extends Visitor {
    private int identificationTypeId;
    private String documentFolio;

    public ExternalVisitor() {
        super();
    }

    public ExternalVisitor(String firstName, String lastName, String email, int identificationTypeId, String documentFolio) {
        super(firstName, lastName, email);
        this.identificationTypeId = identificationTypeId;
        this.documentFolio = documentFolio;
    }

    public int getIdentificationTypeId() { 
        return identificationTypeId; 
    }

    public void setIdentificationTypeId(int identificationTypeId) { 
        this.identificationTypeId = identificationTypeId; 
    }
    
    public String getDocumentFolio() { 
        return documentFolio; 
    }

    public void setDocumentFolio(String documentFolio) { 
        this.documentFolio = documentFolio; 
    }
}
