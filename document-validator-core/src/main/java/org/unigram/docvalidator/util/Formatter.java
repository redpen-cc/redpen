/**
 * redpen: a text inspection tool
 * Copyright (C) 2014 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unigram.docvalidator.util;

public interface Formatter {
  /**
   * Convert ValidationError into a string to flush a error message.
   *
   * @param error object containing file and line number information.
   * @return error message
   */
  public String convertError(ValidationError error);

  /**
   * Return the header block of semi-structured format.
   *
   * @return header block
   */
  public String header();

  /**
   * Return the footer block of semi-structured format.
   *
   * @return
   */
  public String footer();
}
