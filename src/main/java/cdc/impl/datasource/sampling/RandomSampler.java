/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is the FRIL Framework.
 *
 * The Initial Developers of the Original Code are
 * The Department of Math and Computer Science, Emory University and 
 * The Centers for Disease Control and Prevention.
 * Portions created by the Initial Developer are Copyright (C) 2008
 * the Initial Developer. All Rights Reserved.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */ 


package cdc.impl.datasource.sampling;

import java.io.IOException;
import java.util.Random;

import cdc.components.AbstractDataSource;
import cdc.datamodel.DataRow;
import cdc.utils.RJException;

public class RandomSampler implements DataSampleInterface {

	private AbstractDataSource source;
	private double percentage;
	private Random random = new Random(System.currentTimeMillis());
	private DataRow prevRow = null;
	private boolean output = false;
	
	private DataRow[] data;
	private int next = 0;
	
	public RandomSampler(AbstractDataSource source, int percentage) throws RJException, IOException {
		this.source = source;
		this.percentage = percentage / (double)100;
		source.reset();
	}
	
	public RandomSampler(DataRow[] dataRows, double percent) {
		data = dataRows;
	}

	public void close() throws IOException, RJException {
		source.close();
	}

	public DataRow getNextRow() throws IOException, RJException {
		while (true) {
			DataRow row = nextRow();
			if (row == null) {
				if (!output) {
					output = true;
					return prevRow;
				}
				return null;
			}
			prevRow = row;
			if (percentage > random.nextDouble()) {
				output = true;
				return row;
			}
		}
	}
	
	private DataRow nextRow() throws IOException, RJException {
		if (source != null) {
			return source.getNextRow();
		} else if (data.length < next) {
			return data[next++];
		} else {
			return null;
		}
	}

	public String getInnerName() {
		return source.getSourceName();
	}

}
