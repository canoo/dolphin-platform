/*
 * Copyright 2015-2017 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.canoo.platform.client;

public interface HttpStatus {

	int HTTP_OK = 200;

	int SC_REQUEST_TIMEOUT = 408;

	int SC_MULTIPLE_CHOICES = 300;

	int SC_MOVED_PERMANENTLY = 301;

	int SC_FOUND = 302;

	int SC_NOT_MODIFIED = 304;

	int SC_TEMPORARY_REDIRECT = 307;

	int SC_BAD_REQUEST = 400;

	int SC_HTTP_UNAUTHORIZED = 401;

	int SC_HTTP_FORBIDDEN = 403;

	int SC_HTTP_RESOURCE_NOTFOUND = 404;

	int SC_GONE = 410;

	int SC_INTERNAL_SERVER_ERROR = 500;

	int SC_NOT_IMPLMENTED = 501;

	int SC_SERVICE_UNAVAILABLE = 503;

	int SC_PERMISSION_DENIED = 550;
}
