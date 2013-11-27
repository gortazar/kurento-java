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
package com.kurento.kmf.media.internal;

import static com.kurento.kms.thrift.api.KmsMediaRecorderEndPointTypeConstants.CONSTRUCTOR_PARAMS_DATA_TYPE;
import static com.kurento.kms.thrift.api.KmsMediaRecorderEndPointTypeConstants.TYPE_NAME;

import java.net.URI;
import java.util.Map;

import com.kurento.kmf.media.Continuation;
import com.kurento.kmf.media.MediaPipeline;
import com.kurento.kmf.media.MediaProfileSpecType;
import com.kurento.kmf.media.RecorderEndPoint;
import com.kurento.kmf.media.internal.refs.MediaElementRef;
import com.kurento.kmf.media.params.MediaParam;
import com.kurento.kmf.media.params.internal.RecorderEndPointConstructorParam;

@ProvidesMediaElement(type = TYPE_NAME)
public class RecorderEndPointImpl extends AbstractUriEndPoint implements
		RecorderEndPoint {

	public RecorderEndPointImpl(MediaElementRef endpointRef) {
		super(endpointRef);
	}

	/**
	 * @param objectRef
	 * @param params
	 */
	public RecorderEndPointImpl(MediaElementRef objectRef,
			Map<String, MediaParam> params) {
		super(objectRef, params);
	}

	/* SYNC */
	@Override
	public void record() {
		start();
	}

	/* ASYNC */
	@Override
	public void record(final Continuation<Void> cont) {
		start(cont);
	}

	static class RecorderEndPointBuilderImpl<T extends RecorderEndPointBuilderImpl<T>>
			extends AbstractUriEndPointBuilder<T, RecorderEndPoint> implements
			RecorderEndPointBuilder {

		private final RecorderEndPointConstructorParam param = new RecorderEndPointConstructorParam();

		public RecorderEndPointBuilderImpl(final URI uri,
				final MediaPipeline pipeline) {
			super(uri, TYPE_NAME, pipeline);
		}

		@Override
		public T withMediaProfile(MediaProfileSpecType type) {
			param.setMediaMuxer(type.toThrift());
			params.put(CONSTRUCTOR_PARAMS_DATA_TYPE, param);
			return self();
		}

	}

}
