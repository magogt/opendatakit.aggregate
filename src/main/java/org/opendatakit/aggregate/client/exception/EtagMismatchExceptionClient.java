/*
 * Copyright (C) 2013 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.opendatakit.aggregate.client.exception;

import java.io.Serializable;

public class EtagMismatchExceptionClient extends Exception implements Serializable {

  private static final String ETAG_MISMATCH_EXCEPTION_CLIENT =
      "Etag mismatch";

  /**
	 *
	 */
  private static final long serialVersionUID = 8450102254267803857L;

  private String message;

  public EtagMismatchExceptionClient() {
    super();
    message = ETAG_MISMATCH_EXCEPTION_CLIENT;
  }

  public EtagMismatchExceptionClient(String message) {
    super(message);
    this.message = message;
  }

  public EtagMismatchExceptionClient(Throwable cause) {
    super(cause);
    this.message = ETAG_MISMATCH_EXCEPTION_CLIENT + " (" +
        cause.getMessage() + ")";
  }

  public EtagMismatchExceptionClient(String message, Throwable cause) {
    super(message, cause);
  }

  @Override
  public String getLocalizedMessage() {
    return message;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
