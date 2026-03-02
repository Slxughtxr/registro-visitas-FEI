package mx.uv.fei.domain.entities;

import java.time.LocalDate;
import java.time.LocalTime;

public class Visit {
    private int id;
    private int visitorId;
    private Integer hostId;
    private Integer evidenceId;
    private LocalDate entryDate;
    private LocalTime entryTime;
    private LocalDate exitDate;
    private LocalTime exitTime;
    private String subject;
    private boolean active;
    
    public Visit() {}

    public Visit(int visitorId, Integer hostId, Integer evidenceId, LocalDate entryDate, LocalTime entryTime, String subject, boolean active) {
        this.visitorId = visitorId;
        this.hostId = hostId;
        this.evidenceId = evidenceId;
        this.entryDate = entryDate;
        this.entryTime = entryTime;
        this.subject = subject;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(int visitorId) {
        this.visitorId = visitorId;
    }

    public Integer getHostId() {
        return hostId;
    }

    public void setHostId(Integer hostId) {
        this.hostId = hostId;
    }

    public Integer getEvidenceId() {
        return evidenceId;
    }

    public void setEvidenceId(Integer evidenceId) {
        this.evidenceId = evidenceId;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public LocalTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalTime entryTime) {
        this.entryTime = entryTime;
    }

    public LocalDate getExitDate() {
        return exitDate;
    }

    public void setExitDate(LocalDate exitDate) {
        this.exitDate = exitDate;
    }

    public LocalTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalTime exitTime) {
        this.exitTime = exitTime;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}