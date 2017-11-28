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
package com.axelor.apps.stock.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.axelor.apps.stock.db.LogisticalForm;
import com.axelor.apps.stock.db.StockMove;
import com.axelor.apps.stock.db.StockMoveLine;
import com.axelor.apps.stock.exception.LogisticalFormWarning;
import com.axelor.exception.AxelorException;
import com.axelor.apps.stock.exception.LogisticalFormError;

/**
 * @author axelor
 *
 */
public interface LogisticalFormService {

	/**
	 * Add detail lines from the stock move.
	 * If there were no lines, add a parcel line first.
	 * 
	 * @param logisticalForm
	 * @param stockMove
	 * @throws AxelorException
	 */
	void addDetailLines(LogisticalForm logisticalForm, StockMove stockMove) throws AxelorException;

	/**
	 * Add parcel or pallet line.
	 * 
	 * @param logisticalForm
	 * @param typeSelect
	 */
	void addParcelPalletLine(LogisticalForm logisticalForm, int typeSelect);

	/**
	 * Compute totals.
	 * 
	 * @param logisticalForm
	 * @throws LogisticalFormError
	 */
	void computeTotals(LogisticalForm logisticalForm) throws LogisticalFormError;

	/**
	 * Check lines.
	 * 
	 * @param logisticalForm
	 * @throws LogisticalFormWarning
	 * @throws LogisticalFormError
	 */
	void checkLines(LogisticalForm logisticalForm) throws LogisticalFormWarning, LogisticalFormError;

	/**
	 * Check for invalid dimensions in lines.
	 * 
	 * @param logisticalForm
	 * @throws LogisticalFormError
	 */
	void checkInvalidLineDimensions(LogisticalForm logisticalForm) throws LogisticalFormError;

	/**
	 * Get list of full spread stock move lines.
	 * 
	 * @param logisticalForm
	 * @return
	 */
	List<StockMoveLine> getFullySpreadStockMoveLineList(LogisticalForm logisticalForm);

	/**
	 * Get map of spreadable quantity for each stock move line.
	 * 
	 * @param logisticalForm
	 * @return
	 */
	Map<StockMoveLine, BigDecimal> getSpreadableQtyMap(LogisticalForm logisticalForm);

	/**
	 * Get map of spread quantity for each stock move line.
	 * 
	 * @param logisticalForm
	 * @return
	 */
	Map<StockMoveLine, BigDecimal> getSpreadQtyMap(LogisticalForm logisticalForm);

	/**
	 * Get domain for stock move.
	 * 
	 * @param logisticalForm
	 * @return
	 */
	String getStockMoveDomain(LogisticalForm logisticalForm);

	/**
	 * Get next parcel/pallet number.
	 * 
	 * @param logisticalForm
	 * @param typeSelect
	 * @return
	 */
	int getNextParcelPalletNumber(LogisticalForm logisticalForm, int typeSelect);

	/**
	 * Get next line sequence.
	 * 
	 * @param logisticalForm
	 * @return
	 */
	int getNextLineSequence(LogisticalForm logisticalForm);

	/**
	 * Sort lines by sequence.
	 * 
	 * @param logisticalForm
	 */
	void sortLines(LogisticalForm logisticalForm);

	/**
	 * Get the list of logistical form IDs for the given stock move.
	 * 
	 * @param stockMove
	 * @return
	 * @throws AxelorException
	 */
	List<Long> getIdList(StockMove stockMove) throws AxelorException;

}