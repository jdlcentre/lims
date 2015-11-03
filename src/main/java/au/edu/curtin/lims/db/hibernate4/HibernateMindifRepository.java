/*
 * Copyright Curtin University, 2015.
 */
// DO NOT MODIFY THIS FILE BY HAND. IT WAS GENERATED BY generate_orm_files.py
package au.edu.curtin.lims.db.hibernate4;

import javax.inject.Inject;

import au.edu.curtin.lims.db.IMindifRepository;
import au.edu.curtin.lims.domain.Mindif;

import org.hibernate.SessionFactory;

import org.springframework.stereotype.Repository;

@Repository
public class HibernateMindifRepository extends BaseHibernateRepository<Mindif> implements IMindifRepository {
    @Inject
    public HibernateMindifRepository(SessionFactory sessionFactory) {
        super(Mindif.class, sessionFactory);
    }

    public Mindif save(Mindif mindif) {
        this.currentSession().save(mindif);
        return mindif;
//        return new Mindif(
//            (Integer) id,
//            mindif.getGeosampleId(),
//            mindif.getMeasurementGuid(),
//            mindif.getSoftwareVersion(),
//            mindif.getViewFieldUm(),
//            mindif.getImageWidthPx(),
//            mindif.getImageHeightPx(),
//            mindif.getSampleDiameterUm(),
//            mindif.getCollectionDate()
//);
    }
}