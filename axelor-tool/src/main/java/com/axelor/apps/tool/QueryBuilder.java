/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2017 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.axelor.apps.tool;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.axelor.db.Model;
import com.axelor.db.Query;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class QueryBuilder<T extends Model> {
    private final List<String> filterList;
    private final List<Pair<String, Object>> bindingList;
    private final Class<T> modelClass;

    private QueryBuilder(Class<T> modelClass) {
        filterList = new ArrayList<>();
        bindingList = new ArrayList<>();
        this.modelClass = modelClass;
    }

    /**
     * Get a query builder.
     * 
     * @param modelClass
     * @return
     */
    public static <T extends Model> QueryBuilder<T> of(Class<T> modelClass) {
        return new QueryBuilder<>(modelClass);
    }

    /**
     * Add filter.
     * 
     * @param filter
     * @return
     */
    public QueryBuilder<T> add(String filter) {
        filterList.add(filter);
        return this;
    }

    /**
     * Add binding.
     * 
     * @param name
     * @param value
     * @return
     */
    public QueryBuilder<T> bind(String name, Object value) {
        bindingList.add(Pair.of(name, value));
        return this;
    }

    /**
     * Create query.
     * 
     * @return
     */
    public Query<T> create() {
        String filter = Joiner.on(" AND ").join(Lists.transform(filterList, input -> String.format("(%s)", input)));
        Query<T> query = Query.of(modelClass).filter(filter);

        for (Pair<String, Object> binding : bindingList) {
            query.bind(binding.getLeft(), binding.getRight());
        }

        return query;
    }

}