/*
 * Copyright Curtin University, 2015.
 */
// DO NOT MODIFY THIS FILE BY HAND. IT WAS GENERATED BY generate_orm_files.py
package au.edu.curtin.lims.db.hibernate4;

import javax.inject.Inject;

import au.edu.curtin.lims.db.IMindifFieldRepository;
import au.edu.curtin.lims.domain.MindifField;

import org.hibernate.SessionFactory;

import org.springframework.stereotype.Repository;

@Repository
public class HibernateMindifFieldRepository extends BaseHibernateRepository<MindifField> implements IMindifFieldRepository {
    @Inject
    public HibernateMindifFieldRepository(SessionFactory sessionFactory) {
        super(MindifField.class, sessionFactory);
    }

    public MindifField save(MindifField mindifField) {
        this.currentSession().save(mindifField);
        return mindifField;
//        return new MindifField(
//            (Integer) id,
//            mindifField.getMindifId(),
//            mindifField.getMindifFieldName(),
//            mindifField.getPositionXPx(),
//            mindifField.getPositionYPx()
//);
    }
}