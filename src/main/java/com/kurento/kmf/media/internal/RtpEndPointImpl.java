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

import static com.kurento.kms.thrift.api.KmsMediaRtpEndPointTypeConstants.TYPE_NAME;

import java.util.Map;

import com.kurento.kmf.media.MediaPipeline;
import com.kurento.kmf.media.RtpEndPoint;
import com.kurento.kmf.media.internal.refs.MediaElementRef;
import com.kurento.kmf.media.params.MediaParam;

@ProvidesMediaElement(type = TYPE_NAME)
public class RtpEndPointImpl extends SdpEndPointImpl implements RtpEndPoint {

	public RtpEndPointImpl(MediaElementRef endpointRef) {
		super(endpointRef);
	}

	/**
	 * @param objectRef
	 * @param params
	 */
	public RtpEndPointImpl(MediaElementRef objectRef,
			Map<String, MediaParam> params) {
		super(objectRef, params);
	}

	public static class RtpEndPointBuilderImpl<T extends RtpEndPointBuilderImpl<T>>
			extends AbstractSdpEndPointBuilder<T, RtpEndPoint> implements
			RtpEndPointBuilder {

		public RtpEndPointBuilderImpl(final MediaPipeline pipeline) {
			super(TYPE_NAME, pipeline);
		}

	}
}
