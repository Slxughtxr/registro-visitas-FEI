package mx.uv.fei.domain.dto;

public class Pagination {
    private final int pageNumber;
    private final int pageSize;

    public Pagination(int pageNumber, int pageSize) {
        this.pageNumber = Math.max(pageNumber, 1);
        this.pageSize = pageSize > 0 ? pageSize : 20; 
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getOffset() {
        return (pageNumber - 1) * pageSize;
    }
}