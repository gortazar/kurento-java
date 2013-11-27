/*
 * (C) Copyright 2013 Kurento (http://kurento.org/)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package com.kurento.kmf.media;

public interface Continuation<F> {

	/**
	 * This method is called when the operation succeeds
	 * 
	 * @param result
	 */
	void onSuccess(F result);

	/**
	 * This method gets called when the operation fails
	 * 
	 * @param cause
	 *            The cause of the failure
	 */
	void onError(Throwable cause);

}
