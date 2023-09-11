/*
 * Copyright 2020-2023 Google LLC
 * Copyright 2020-2023 EPAM Systems, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package legal.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;

@Slf4j
public class CustomHttpClientResponseHandler
    implements HttpClientResponseHandler<CloseableHttpResponse> {

  @Override
  public CloseableHttpResponse handleResponse(ClassicHttpResponse classicHttpResponse) {
    HttpEntity entity = classicHttpResponse.getEntity();
    if (classicHttpResponse.getCode() != HttpStatus.SC_NO_CONTENT) {
      String body = "";
      try {
        body = EntityUtils.toString(entity);
      } catch (IOException | ParseException e) {
        log.error("unable to parse response");
      }
      HttpEntity newEntity = new StringEntity(body, ContentType.parse(entity.getContentType()));
      classicHttpResponse.setEntity(newEntity);
    }
    return (CloseableHttpResponse) classicHttpResponse;
  }
}
