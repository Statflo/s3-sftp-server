package com.dataexchange.server.jpa.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "file_events")
public class FileEventEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "fe_id")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fe_au_id", nullable = false)
    private AuthUserEntity authUser;

    @Column(name = "fe_filename", nullable = false)
    private String filename;

    @Column(name = "fe_action")
    private String action;

    @Column(name = "fe_date_started", nullable = false)
    private Date dateStarted;

    @Column(name = "fe_date_finished")
    private Date dateFinished;

    public Long getId() {
        return id;
    }

    public AuthUserEntity getAuthUser() {
        return authUser;
    }

    public void setAuthUser(AuthUserEntity authUser) {
        this.authUser = authUser;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Date getDateStarted() {
        return dateStarted;
    }

    public void setDateStarted(Date dateStarted) {
        this.dateStarted = dateStarted;
    }

    public Date getDateFinished() {
        return dateFinished;
    }

    public void setDateFinished(Date dateFinished) {
        this.dateFinished = dateFinished;
    }
}
