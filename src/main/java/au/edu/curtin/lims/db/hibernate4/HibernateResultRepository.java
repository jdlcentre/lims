/*
 * Copyright Curtin University, 2015.
 */
// DO NOT MODIFY THIS FILE BY HAND. IT WAS GENERATED BY generate_orm_files.py
package au.edu.curtin.lims.db.hibernate4;

import javax.inject.Inject;

import au.edu.curtin.lims.db.IResultRepository;
import au.edu.curtin.lims.domain.Result;

import org.hibernate.SessionFactory;

import org.springframework.stereotype.Repository;

@Repository
public class HibernateResultRepository extends BaseHibernateRepository<Result> implements IResultRepository {
    @Inject
    public HibernateResultRepository(SessionFactory sessionFactory) {
        super(Result.class, sessionFactory);
    }

    public Result save(Result result) {
        this.currentSession().save(result);
        return result;
//        return new Result(
//            (Integer) id,
//            result.getAnalysisRequestId(),
//            result.getGeosampleId(),
//            result.getValue(),
//            result.getHasStagedFile()
//);
    }
}
