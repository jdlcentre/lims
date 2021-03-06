/*
 * Copyright Curtin University, 2015.
 */
// DO NOT MODIFY THIS FILE BY HAND. IT WAS GENERATED BY generate_orm_files.py
package au.edu.curtin.lims.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "mindif_field")
@SequenceGenerator(name = "mindif_field_mindif_field_id_seq", sequenceName = "mindif_field_mindif_field_id_seq", allocationSize = 1)
public class MindifField implements Comparable<MindifField> {
    private int mindifFieldId;

    private Mindif mindif;

    private String mindifFieldName;

    private int positionXPx;

    private int positionYPx;

    private List<MineralOccurrence> mineralOccurrences;

    public MindifField() { }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mindif_field_mindif_field_id_seq")
    @Column(name = "mindif_field_id", nullable = false)
    public Integer getMindifFieldId() {
        return this.mindifFieldId;
    }
    
    @SuppressWarnings("unused")
    private void setMindifFieldId(Integer mindifFieldId) {
        this.mindifFieldId = mindifFieldId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mindif_id")
    public Mindif getMindif() {
        return mindif;
    }
    
    public void setMindif(Mindif mindif) {
        this.mindif = mindif;
    }

    @Column(name = "mindif_field_name", nullable = false, length = 4)
    public String getMindifFieldName() {
        return this.mindifFieldName;
    }
    
    public void setMindifFieldName(String mindifFieldName) {
        this.mindifFieldName = mindifFieldName;
    }

    @Column(name = "position_x_px", nullable = false, length = 4)
    public int getPositionXPx() {
        return this.positionXPx;
    }
    
    public void setPositionXPx(int positionXPx) {
        this.positionXPx = positionXPx;
    }

    @Column(name = "position_y_px", nullable = false, length = 4)
    public int getPositionYPx() {
        return this.positionYPx;
    }
    
    public void setPositionYPx(int positionYPx) {
        this.positionYPx = positionYPx;
    }

    @OneToMany(
        mappedBy = "mindifField",
        fetch = FetchType.LAZY)
    @OrderBy("mineral_occurrence_id")
    public List<MineralOccurrence> getMineralOccurrences() {
        return this.mineralOccurrences;
    }
    
    public void setMineralOccurrences(List<MineralOccurrence> mineralOccurrences) {
        this.mineralOccurrences = mineralOccurrences;
    }

    @Override
    public int compareTo(MindifField o) {
        return this.getMindifFieldId().compareTo(o.getMindifFieldId());
    }
}
