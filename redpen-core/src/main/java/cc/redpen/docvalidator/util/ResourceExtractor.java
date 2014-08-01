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
package cc.redpen.docvalidator.util;

/**
 * ResourceExtractor is called from FileLoader. To support a file format,
 * we create a class implementing ResourceExtractor.
 */
public interface ResourceExtractor {
  /**
   * load line.
   *
   * @param line line in a file
   * @return 0 when succeeded, otherwise 1
   */
  int load(String line);
}
