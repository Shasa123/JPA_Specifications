package com.jpa.learning.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jpa.learning.entity.FilterCriteria;
import com.jpa.learning.entity.User;
import com.jpa.learning.exception.CustomException;
import com.jpa.learning.operation.SearchOperation;
import com.jpa.learning.repository.UserRepository;

@Service
public class UserService {

    public enum CustomFieldSupportedDatatype {

        DATE, STRING, INTEGER;
    }
    
    //In case of no terminal list
    private static final int NON_EXISTENT_TERMINAL_KEY= -999;
    
    private final UserRepository userRepository;

    private EntityManager entityManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository, EntityManager entityManager) {
        super();
        this.userRepository = userRepository;
        this.entityManager = entityManager;
    }

    public List<User> findAll() {
        return (List<User>) userRepository.findAll();
    }

    public void addUser(User user) {
        userRepository.save(user);
    }

    public <T> Subquery<T> createParentQuery(
            Class<T> t,
            List<FilterCriteria> criterialist) throws NoSuchFieldException, IllegalAccessException {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<T> root = criteriaQuery.from(t);

        Subquery<T> subquery = criteriaQuery.subquery(t);
        Root<T> rootSubquery = subquery.from(t);

        Long key = 227L;

        FilterCriteria criteria2 = criterialist.get(0);

        subquery.select(rootSubquery.get("entityId"));
        subquery.where(createPredicateForEqual(criteria2, criteriaBuilder, rootSubquery, key));

        criterialist.remove(0);
        Subquery<T> newSubquery = null;
        for (FilterCriteria criteria : criterialist) {
            Predicate newPredicate = null;
            //            if (criteria.getOperation().equals(SearchOperation.EQUALS_TO.toString())) {
            //                newPredicate = createPredicateForEqual(criteria, criteriaBuilder, rootSubquery, key);
            //            } else if (criteria.getOperation().equals(SearchOperation.NOT_EQUALS_TO.toString())) {
            //                newPredicate = createPredicateForNotEqual(criteria, criteriaBuilder, rootSubquery, key);
            //            } else if (criteria.getOperation().equals(SearchOperation.STARTS_WITH.toString())) {
            //                newPredicate = createPredicateForStartsWith(criteria, criteriaBuilder, rootSubquery, key);
            //            } else if (criteria.getOperation().equals(SearchOperation.GREATER_THAN.toString())) {
            //                newPredicate = createPredicateForGreaterThan(criteria, criteriaBuilder, rootSubquery, key);
            //            } else if (criteria.getOperation().equals(SearchOperation.LESS_THAN.toString())) {
            //                newPredicate = createPredicateForLessThan(criteria, criteriaBuilder, rootSubquery, key);
            //            } else if (criteria.getOperation().equals(SearchOperation.GREATER_THAN_OR_EQUALS_TO.toString())) {
            //                newPredicate = createPredicateForGreaterThanOrEqual(criteria, criteriaBuilder, rootSubquery, key);
            //            } else if (criteria.getOperation().equals(SearchOperation.LESS_THAN_OR_EQUALS_TO.toString())) {
            //                newPredicate = createPredicateForLessThanOrEqual(criteria, criteriaBuilder, rootSubquery, key);
            //            }

            //            newSubquery = criteriaQuery.subquery(t);
            //            newSubquery.select(rootSubquery.get("entityId"));
            //            newSubquery.where(criteriaBuilder.and(newPredicate,
            //                            criteriaBuilder.equal(rootSubquery.get("entityId"), subquery)));

            newSubquery = criteriaQuery.subquery(t);
            newSubquery.select(rootSubquery.get("entityId"));
            newSubquery
                    .where(
                            criteriaBuilder
                                    .and(
                                            newPredicate,
                                            rootSubquery.get("entityId").in(subquery)));

            subquery = newSubquery;

        }

        criteriaQuery.select(root.get("entityId"));
        //    criteriaQuery.where(criteriaBuilder.equal(root.get("entityId"), subquery));
        criteriaQuery.where(root.get("entityId").in(subquery));

        //    CriteriaImpl c = (CriteriaImpl) criteriaQuery;
        //    SessionImpl s = (SessionImpl) c.getSession();
        //    SessionFactoryImplementor factory = (SessionFactoryImplementor) s.getSessionFactory();
        //    String[] implementors = factory.getImplementors(c.getEntityOrClassName());
        //    LoadQueryInfluencers lqis = new LoadQueryInfluencers();
        //    CriteriaLoader loader = new CriteriaLoader((OuterJoinLoadable) factory.getEntityPersister(implementors[0]), factory, c, implementors[0], lqis);
        //    Field f = OuterJoinLoader.class.getDeclaredField("sql");
        //    f.setAccessible(true);
        //    String sql = (String) f.get(loader);
        //    System.out.println(sql);
        System.out.println(criteriaQuery);
        System.out.println(entityManager);
        TypedQuery<User> query = entityManager.createQuery(criteriaQuery);
        System.out.println(query.unwrap(org.hibernate.Query.class).getQueryString());
        List<User> resultList = entityManager.createQuery(criteriaQuery).getResultList();
        resultList.forEach(d -> System.out.println(" list is " + d.toString()));

        System.out.println(query.unwrap(org.hibernate.Query.class).getQueryString());
        //    resultList.forEach(System.out::println);

        return subquery;
    }

    public <T> Predicate createPredicateForEqual(
            FilterCriteria criteria,
            CriteriaBuilder criteriaBuilder,
            Root<T> rootSubquery,
            Long key) {

        return criteriaBuilder
                .and(
                        criteriaBuilder
                                .equal(rootSubquery.get(criteria.getKey()), getParsedFilterCriteriaValue(criteria)),
                        criteriaBuilder.equal(rootSubquery.get("id"), key));
    }

    public <T> Predicate createPredicateForNotEqual(
            FilterCriteria criteria,
            CriteriaBuilder criteriaBuilder,
            Root<T> rootSubquery,
            Long key) {
        return criteriaBuilder
                .and(
                        criteriaBuilder
                                .notEqual(rootSubquery.get(criteria.getKey()), getParsedFilterCriteriaValue(criteria)),
                        criteriaBuilder.equal(rootSubquery.get("id"), key));
    }

    public <T> Predicate createPredicateForStartsWith(
            FilterCriteria criteria,
            CriteriaBuilder criteriaBuilder,
            Root<T> rootSubquery,
            Long key) {

        return criteriaBuilder
                .and(
                        criteriaBuilder
                                .like(
                                        rootSubquery.get(criteria.getKey()),
                                        getParsedFilterCriteriaValue(criteria) + "%"),
                        criteriaBuilder.equal(rootSubquery.get("id"), key));
    }

    public <T> Predicate createPredicateForGreaterThan(
            FilterCriteria criteria,
            CriteriaBuilder criteriaBuilder,
            Root<T> rootSubquery,
            Long key) {
        return criteriaBuilder
                .and(
                        criteriaBuilder
                                .greaterThan(
                                        rootSubquery.get(criteria.getKey()),
                                        getParsedFilterCriteriaValue(criteria).toString()),
                        criteriaBuilder.equal(rootSubquery.get("id"), key));
    }

    public <T> Predicate createPredicateForLessThan(
            FilterCriteria criteria,
            CriteriaBuilder criteriaBuilder,
            Root<T> rootSubquery,
            Long key) {
        return criteriaBuilder
                .and(
                        criteriaBuilder
                                .lessThan(
                                        rootSubquery.get(criteria.getKey()),
                                        getParsedFilterCriteriaValue(criteria).toString()),
                        criteriaBuilder.equal(rootSubquery.get("id"), key));
    }

    public <T> Predicate createPredicateForGreaterThanOrEqual(
            FilterCriteria criteria,
            CriteriaBuilder criteriaBuilder,
            Root<T> rootSubquery,
            Long key) {
        return criteriaBuilder
                .and(
                        criteriaBuilder
                                .greaterThanOrEqualTo(
                                        rootSubquery.get(criteria.getKey()),
                                        getParsedFilterCriteriaValue(criteria).toString()),
                        criteriaBuilder.equal(rootSubquery.get("id"), key));
    }

    public <T> Predicate createPredicateForLessThanOrEqual(
            FilterCriteria criteria,
            CriteriaBuilder criteriaBuilder,
            Root<T> rootSubquery,
            Long key) {
        return criteriaBuilder
                .and(
                        criteriaBuilder
                                .lessThanOrEqualTo(
                                        rootSubquery.get(criteria.getKey()),
                                        getParsedFilterCriteriaValue(criteria).toString()),
                        criteriaBuilder.equal(rootSubquery.get("id"), key));
    }

    public <T> Predicate createPredicateForBetween(
            FilterCriteria criteria,
            CriteriaBuilder criteriaBuilder,
            Root<T> rootSubquery,
            Long key) {

        String criteriaValue = (String) criteria.getValue();
        String[] splitValue = criteriaValue.split(",");
        System.out.println(splitValue[0] + " ----------->");
        System.out.println(splitValue[1] + " ----------->");
        if (criteria.getType().equalsIgnoreCase("date")) {
            return criteriaBuilder
                    .and(
                            criteriaBuilder
                                    .between(
                                            rootSubquery.get(criteria.getKey()),
                                            getDateValue(splitValue[0]),
                                            getDateValue(splitValue[1])),
                            criteriaBuilder.equal(rootSubquery.get("id"), key));
        }

        return criteriaBuilder
                .and(
                        criteriaBuilder
                                .between(
                                        rootSubquery.get(criteria.getKey()),
                                        Integer.parseInt(splitValue[0]),
                                        Integer.parseInt(splitValue[1])),
                        criteriaBuilder.equal(rootSubquery.get("id"), key));

        //        String ob1 = (String) criteria.getValue();
        //        String[] str = ob1.split(",");
        //        return criteriaBuilder
        //                .and(
        //                        criteriaBuilder.between(rootSubquery.get(criteria.getKey()), str[0], str[1]),
        //                        criteriaBuilder.equal(rootSubquery.get("id"), key));
    }

    public <T> Predicate createPredicateForNotBetween(
            FilterCriteria criteria,
            CriteriaBuilder criteriaBuilder,
            Root<T> rootSubquery,
            Long key) {
        String str = (String) criteria.getValue();
        String[] str2 = str.split(",");
        if (criteria.getType().equalsIgnoreCase("date")) {
            return criteriaBuilder
                    .and(
                            criteriaBuilder
                                    .or(
                                            criteriaBuilder
                                                    .lessThanOrEqualTo(
                                                            rootSubquery.get(criteria.getKey()),
                                                            getDateValue(str2[0])),
                                            criteriaBuilder
                                                    .greaterThanOrEqualTo(
                                                            rootSubquery.get(criteria.getKey()),
                                                            getDateValue(str2[1]))),

                            criteriaBuilder.equal(rootSubquery.get("id"), key));
        }
        return criteriaBuilder
                .and(
                        criteriaBuilder.and(criteriaBuilder
                                .lessThanOrEqualTo(
                                        rootSubquery.get(criteria.getKey()),
                                        getParsedFilterCriteriaValue(criteria).toString()),
                                criteriaBuilder
                                .greaterThanOrEqualTo(
                                        rootSubquery.get(criteria.getKey()),
                                        getParsedFilterCriteriaValue(criteria).toString())
                                ),
                        criteriaBuilder.equal(rootSubquery.get("id"), key));
    }

    public <T> Predicate createPredicateForBefore(
            FilterCriteria criteria,
            CriteriaBuilder criteriaBuilder,
            Root<T> rootSubquery,
            Long key) {
        return criteriaBuilder
                .and(
                        criteriaBuilder
                                .lessThan(
                                        rootSubquery.get(criteria.getKey()),
                                        (Date) getParsedFilterCriteriaValue(criteria)),
                        criteriaBuilder.equal(rootSubquery.get("id"), key));
    }

    public <T> Predicate createPredicateForOnOrBefore(
            FilterCriteria criteria,
            CriteriaBuilder criteriaBuilder,
            Root<T> rootSubquery,
            Long key) {
        return criteriaBuilder
                .and(
                        criteriaBuilder
                                .lessThanOrEqualTo(
                                        rootSubquery.get(criteria.getKey()),
                                        getDateValue((String) criteria.getValue())),
                        criteriaBuilder.equal(rootSubquery.get("id"), key));
    }

    public <T> Predicate createPredicateForAfter(
            FilterCriteria criteria,
            CriteriaBuilder criteriaBuilder,
            Root<T> rootSubquery,
            Long key) {
        return criteriaBuilder
                .and(
                        criteriaBuilder
                                .greaterThan(
                                        rootSubquery.get(criteria.getKey()),
                                        getDateValue((String) criteria.getValue())),
                        criteriaBuilder.equal(rootSubquery.get("id"), key));
    }

    public <T> Predicate createPredicateForOnOrAfter(
            FilterCriteria criteria,
            CriteriaBuilder criteriaBuilder,
            Root<T> rootSubquery,
            Long key) {
        return criteriaBuilder
                .and(
                        criteriaBuilder
                                .greaterThanOrEqualTo(
                                        rootSubquery.get(criteria.getKey()),
                                        getDateValue((String) criteria.getValue())),
                        criteriaBuilder.equal(rootSubquery.get("id"), key));
    }

    //public <T> Subquery<T> createQuery(Class<T> t,
    //        List<FilterCriteria> criterialist) throws NoSuchFieldException,IllegalAccessException{
    //    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    //CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
    //Root<User> root = criteriaQuery.from(User.class);
    //
    //Subquery<T> subquery = criteriaQuery.subquery(t);
    //Root<T> rootSubquery = subquery.from(t);
    //
    //Long key = 227L;
    //
    //FilterCriteria criteria2 = criterialist.get(0);
    //
    //subquery.select(rootSubquery.get("entityId"));
    //subquery.where(createPredicateForEqual(criteria2, criteriaBuilder, rootSubquery, key));
    //
    //criterialist.remove(0);
    //Subquery<T> newSubquery = null;
    //for (FilterCriteria criteria : criterialist) {
    //        Predicate newPredicate = null;
    //        newSubquery = criteriaQuery.subquery(t);
    //        rootSubquery = newSubquery.from(t);
    //if(criteria.getOperation().equals(SearchOperation.EQUALS_TO.toString())) {
    //newPredicate = createPredicateForEqual(criteria, criteriaBuilder, rootSubquery, 228L);
    //}
    //else if(criteria.getOperation().equals(SearchOperation.NOT_EQUALS_TO.toString())) {
    //        newPredicate = createPredicateForNotEqual(criteria, criteriaBuilder, rootSubquery, key);
    //    }
    //else if(criteria.getOperation().equals(SearchOperation.STARTS_WITH.toString())) {
    //        newPredicate = createPredicateForStartsWith(criteria, criteriaBuilder, rootSubquery, key);
    //    }
    //else if(criteria.getOperation().equals(SearchOperation.GREATER_THAN.toString())) {
    //        newPredicate = createPredicateForGreaterThan(criteria, criteriaBuilder, rootSubquery, key);
    //    }
    //else if(criteria.getOperation().equals(SearchOperation.LESS_THAN.toString())) {
    //        newPredicate = createPredicateForLessThan(criteria, criteriaBuilder, rootSubquery, key);
    //    }
    //else if(criteria.getOperation().equals(SearchOperation.GREATER_THAN_OR_EQUALS_TO.toString())) {
    //        newPredicate = createPredicateForGreaterThanOrEqual(criteria, criteriaBuilder, rootSubquery, key);
    //    }
    //else if(criteria.getOperation().equals(SearchOperation.LESS_THAN_OR_EQUALS_TO.toString())) {
    //        newPredicate = createPredicateForLessThanOrEqual(criteria, criteriaBuilder, rootSubquery, key);
    //    }
    ////        newSubquery.select(rootSubquery.get("entityId"));
    ////        newSubquery.where(criteriaBuilder.and(newPredicate,
    ////                        criteriaBuilder.equal(rootSubquery.get("entityId"), subquery)));
    //    
    //newSubquery.select(rootSubquery.get("entityId"));
    //newSubquery.where(criteriaBuilder.and(newPredicate,
    //        rootSubquery.get("entityId").in(subquery)));
    //        subquery = newSubquery;
    //
    //}
    //
    //
    //
    ////criteriaQuery.select(root.get("entityId"));
    ////criteriaQuery.where(criteriaBuilder.equal(root.get("entityId"), key));
    ////criteriaQuery.where(criteriaBuilder.equal(root.get("entityId"), subquery));
    //criteriaQuery.where(root.get("entityId").in(subquery));
    //
    //
    //System.out.println(criteriaQuery);
    //System.out.println(entityManager);
    //TypedQuery<User> query = entityManager.createQuery(criteriaQuery);
    //System.out.println(query.unwrap(org.hibernate.Query.class).getQueryString());
    //List<User> resultList = entityManager.createQuery(criteriaQuery).getResultList();
    //resultList.forEach(d -> System.out.println(" list is "+d.getName()));
    //
    //
    //System.out.println(query.unwrap(org.hibernate.Query.class).getQueryString());
    ////resultList.forEach(System.out::println);
    //
    //return subquery;
    //}

    private <T> Subquery<T> createQuery(
            Class<T> t,
            List<FilterCriteria> criterialist,
            String fieldName) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(t);
        Root<T> root = criteriaQuery.from(t);

        Subquery<T> subquery = criteriaQuery.subquery(t);
        Root<T> rootSubquery;

        int count = 0;

        Long key = 227L;

        Subquery<T> newSubquery = null;
        for (FilterCriteria criteria : criterialist) {
            Predicate newPredicate = null;
            newSubquery = criteriaQuery.subquery(t);
            rootSubquery = newSubquery.from(t);
            newPredicate = getConditionalPredicate(criteria, criteriaBuilder, rootSubquery, key);
            newSubquery.select(rootSubquery.get(fieldName));
            if (count++ == 0) {
                newSubquery.where(newPredicate);
            } else {
                newSubquery.select(rootSubquery.get(fieldName));
                newSubquery
                        .where(
                                criteriaBuilder
                                        .and(
                                                newPredicate,
                                                rootSubquery.get("entityId").in(subquery)));
            }
            if (count > 0) {
                key = 228L;
            }
            subquery = newSubquery;
        }

        criteriaQuery.select(root.get("entityId"));
        criteriaQuery.where(root.get(fieldName).in(subquery));

        TypedQuery<T> query = entityManager.createQuery(criteriaQuery);
        System.out.println(query.unwrap(org.hibernate.Query.class).getQueryString());

        List<T> resultList = query.getResultList();
        System.out.println("size is " + resultList.size());
        resultList.forEach(d -> System.out.println(" list is " + d.toString()));

        //        CriteriaQuery<Person> criteriaQueryPerson = criteriaBuilder.createQuery(Person.class);
        //        Root<Person> rootPerson = criteriaQueryPerson.from(Person.class);
        //        criteriaQueryPerson.where(rootPerson.get("entityId").in(subquery));
        //
        //        TypedQuery<Person> query2 = entityManager.createQuery(criteriaQueryPerson);
        //        System.out.println(query2.unwrap(org.hibernate.Query.class).getQueryString());

        //        List<Person> resultList2 = query2.getResultList();
        //        resultList2.forEach(d -> System.out.println(" list is " + d.toString()));

        return subquery;
    }

    private <T> CriteriaQuery<Integer> createQuery2(
            Class<T> t,
            List<FilterCriteria> criterialist,
            String fieldName) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Integer> criteriaQuery = criteriaBuilder.createQuery(Integer.class);
        Root<T> root = criteriaQuery.from(t);

        Subquery<T> subquery = criteriaQuery.subquery(t);
        Root<T> rootSubquery;

        int count = 0;

        Long key = 227L;

        Subquery<T> newSubquery = null;
        for (FilterCriteria criteria : criterialist) {
            Predicate newPredicate = null;
            newSubquery = criteriaQuery.subquery(t);
            rootSubquery = newSubquery.from(t);
            newPredicate = getConditionalPredicate(criteria, criteriaBuilder, rootSubquery, key);
            newSubquery.select(rootSubquery.get(fieldName));
            if (count++ == 0) {
                newSubquery.where(newPredicate);
            } else {
                newSubquery.select(rootSubquery.get(fieldName));
                newSubquery
                        .where(
                                criteriaBuilder
                                        .and(
                                                newPredicate,
                                                rootSubquery.get("entityId").in(subquery)));
            }
            if (count > 0) {
                key = 228L;
            }
            subquery = newSubquery;
        }

        criteriaQuery.select(root.get("entityId"));
        criteriaQuery.where(root.get(fieldName).in(subquery));

        TypedQuery<Integer> query = entityManager.createQuery(criteriaQuery);
        System.out.println(query.unwrap(org.hibernate.Query.class).getQueryString());

        List<Integer> resultList = query.getResultList();
        Set<Integer> valueResult = new HashSet<>(resultList);
        System.out.println("size is " + valueResult.size());
        valueResult.forEach(d -> System.out.println(" list is " + d.toString()));

        //        CriteriaQuery<Person> criteriaQueryPerson = criteriaBuilder.createQuery(Person.class);
        //        Root<Person> rootPerson = criteriaQueryPerson.from(Person.class);
        //        criteriaQueryPerson.where(rootPerson.get("entityId").in(subquery));
        //
        //        TypedQuery<Person> query2 = entityManager.createQuery(criteriaQueryPerson);
        //        System.out.println(query2.unwrap(org.hibernate.Query.class).getQueryString());

        //        List<Person> resultList2 = query2.getResultList();
        //        resultList2.forEach(d -> System.out.println(" list is " + d.toString()));

        return criteriaQuery;
    }

    public <T> CriteriaQuery<T> getParentQuery(Class<T> t, List<FilterCriteria> criterialist, String fieldName) {
        Subquery<?> subquery = createQuery(User.class, criterialist, fieldName);
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQueryPerson = criteriaBuilder.createQuery(t);
        Root<T> rootPerson = criteriaQueryPerson.from(t);

        criteriaQueryPerson.where(rootPerson.get(fieldName).in(subquery));

        TypedQuery<T> query2 = entityManager.createQuery(criteriaQueryPerson);
        System.out.println(query2.unwrap(org.hibernate.Query.class).getQueryString());

        List<T> resultList2 = query2.getResultList();
        resultList2.forEach(d -> System.out.println(" list is " + d.toString()));

        return criteriaQueryPerson.where(rootPerson.get(fieldName).in(subquery));

    }

    public List<Integer> getParentQuery2(List<FilterCriteria> criterialist, String fieldName) {
        CriteriaQuery<Integer> criteriaQuery = createQuery2(User.class, criterialist, fieldName);
        TypedQuery<Integer> query = entityManager.createQuery(criteriaQuery);
        List<Integer> result = query.getResultList();
        String terminalList = getResultList(result);
        if(result.isEmpty()) {
                terminalList = String.valueOf(NON_EXISTENT_TERMINAL_KEY);
        }
        StringBuilder stringBuilder = new StringBuilder("(").append(terminalList).append(")");
        stringBuilder.insert(0, "Select Id from Person person where person.some1 in");

        Query query2 = entityManager.createNativeQuery(stringBuilder.toString());

        System.out.println("Query is ------> " + query2.toString());
        //        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        //        CriteriaQuery<T> criteriaQueryPerson = criteriaBuilder.createQuery(t);
        //        Root<T> rootPerson = criteriaQueryPerson.from(t);
        //
        //        criteriaQueryPerson.where(rootPerson.get(fieldName).in(criteriaQuery));

        //        TypedQuery<T> query2 = entityManager.createQuery(criteriaQueryPerson);
        //        System.out.println(query2.unwrap(org.hibernate.Query.class).getQueryString());
        //
        //        List<T> resultList2 = query2.getResultList();
        //        resultList2.forEach(d -> System.out.println(" list is " + d.toString()));

        Set<Integer> idList = new HashSet<>();
        for (Object terminal : query2.getResultList()) {
            Integer terminalLink = terminal != null ? Integer.parseInt(terminal.toString()) : null;
            idList.add(terminalLink);
        }
        idList.forEach(System.out::println);
        return new ArrayList<>(idList);
        //        return criteriaQueryPerson.where(rootPerson.get(fieldName).in(criteriaQuery));

    }

    public <T> Predicate getConditionalPredicate(
            FilterCriteria criteria,
            CriteriaBuilder criteriaBuilder,
            Root<T> rootSubquery,
            Long key) {

        if (criteria.getOperation().equals(SearchOperation.EQUALS_TO.toString())) {
            return createPredicateForEqual(criteria, criteriaBuilder, rootSubquery, key);
        } else if (criteria.getOperation().equals(SearchOperation.NOT_EQUALS_TO.toString())) {
            return createPredicateForNotEqual(criteria, criteriaBuilder, rootSubquery, key);
        } else if (criteria.getOperation().equals(SearchOperation.STARTS_WITH.toString())) {
            return createPredicateForStartsWith(criteria, criteriaBuilder, rootSubquery, key);
        } else if (criteria.getOperation().equals(SearchOperation.GREATER_THAN.toString())) {
            return createPredicateForGreaterThan(criteria, criteriaBuilder, rootSubquery, key);
        } else if (criteria.getOperation().equals(SearchOperation.LESS_THAN.toString())) {
            return createPredicateForLessThan(criteria, criteriaBuilder, rootSubquery, key);
        } else if (criteria.getOperation().equals(SearchOperation.GREATER_THAN_OR_EQUALS_TO.toString())) {
            return createPredicateForGreaterThanOrEqual(criteria, criteriaBuilder, rootSubquery, key);
        } else if (criteria.getOperation().equals(SearchOperation.LESS_THAN_OR_EQUALS_TO.toString())) {
            return createPredicateForLessThanOrEqual(criteria, criteriaBuilder, rootSubquery, key);
        } else if (criteria.getOperation().equals(SearchOperation.BEFORE.toString())) {
            return createPredicateForBefore(criteria, criteriaBuilder, rootSubquery, key);
        } else if (criteria.getOperation().equals(SearchOperation.AFTER.toString())) {
            return createPredicateForAfter(criteria, criteriaBuilder, rootSubquery, key);
        } else if (criteria.getOperation().equals(SearchOperation.ON_OR_BEFORE.toString())) {
            return createPredicateForOnOrBefore(criteria, criteriaBuilder, rootSubquery, key);
        } else if (criteria.getOperation().equals(SearchOperation.ON_OR_AFTER.toString())) {
            return createPredicateForOnOrAfter(criteria, criteriaBuilder, rootSubquery, key);
        } else if (criteria.getOperation().equals(SearchOperation.BETWEEN.toString())) {
            return createPredicateForBetween(criteria, criteriaBuilder, rootSubquery, key);
        }else if (criteria.getOperation().equals(SearchOperation.NOT_BETWEEN.toString())) {
            return createPredicateForNotBetween(criteria, criteriaBuilder, rootSubquery, key);
        }
        return null;
    }

    //    public static LocalDateTime getParsedDate() {
    //        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-DD HH:mm:ss");
    //        String dateTimeStringFromSqlite = "29-Apr-2010,13:00:14";
    //        String dateTimeStringFromSqlite2 = "2010-11-10 13:00:14";
    ////        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStringFromSqlite, formatter);
    //        return LocalDateTime.parse(dateTimeStringFromSqlite2,formatter);

    //    }

    private static boolean validateDateOperations(FilterCriteria criteria) {
        return Arrays
                .asList(
                        "BETWEEN",
                        "NOTBETWEEN",
                        "BEFORE",
                        "AFTER",
                        "ON_OR_BEFORE",
                        "ON_OR_AFTER",
                        "EQUALS_TO",
                        "NOT_EQUALS_TO")
                .contains(criteria.getOperation());
    }

    public Object getParsedFilterCriteriaValue(FilterCriteria criteria) {

        if (criteria.getType().equalsIgnoreCase("date"))
            return getDateValue((String) criteria.getValue());

        return criteria.getValue();
    }

    private static Date getDateValue(String value) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .parse(value);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            throw new CustomException();
        }
    }

    public Date getFormattedDate(String date) {
        Date parsedDate = null;
        try {
            parsedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(date);
        } catch (ParseException e) {
        }
        return parsedDate;
    }

    public boolean validateType(String type) {
        for (CustomFieldSupportedDatatype c : CustomFieldSupportedDatatype.values()) {
            if (c.name().equals(type)) {
                return true;
            }
        }
        return false;
    }

    public enum type {
        DATE("date"),
        STRING("string"),
        INTEGER("integer");

        private String message;

        private type(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public static void main(String[] args) throws ParseException {
//        String dateTimeStringFromSqlite2 = "2010-11-10 13:00:14";
//        String dateTimeStringFromSqlite = "10-11-2010 13:00:14";
//        Date date1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(dateTimeStringFromSqlite);
//        Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTimeStringFromSqlite2);
//        //        LocalDate ld = LocalDate.parse("2010-11-10 13:00:14");
//        //        System.out.println(ld.toString());
//        System.out.println(date1);
//        System.out.println(new Date());
//        System.out.println(date2);
//        System.out.println();
//        //        System.out.println(getParsedDate());
//
//        System.out.println(validateDateOperations(new FilterCriteria("1", "c", "1")));
//        System.out.println("string".equalsIgnoreCase(type.STRING.toString()));
//
//        System.out.println(getResultList(Arrays.asList(1, 2, 3, 4)));
        
        System.out.println(getDateValue(""));
    }

    public static String getResultList(List<Integer> list) {
        return list.stream().map(String::valueOf).collect(Collectors.joining(","));
    }
}
