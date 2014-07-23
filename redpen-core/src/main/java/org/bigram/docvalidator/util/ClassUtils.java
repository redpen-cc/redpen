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
package org.bigram.docvalidator.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ClassUtils {

  static public Type getParameterizedClass(Object obj) {
    if (obj == null) {
      return null;
    }

    Class clazz = obj.getClass();
    if (clazz.getGenericInterfaces().length == 0) {
      return null;
    }

    Type genericInterface = clazz.getGenericInterfaces()[0];
    ParameterizedType parameterizedType;
    try {
      parameterizedType =
          ParameterizedType.class.cast(genericInterface);
    } catch (ClassCastException e) {
      return null;
    }

    if (parameterizedType.getActualTypeArguments().length == 0) {
      return null;
    }
    return parameterizedType.getActualTypeArguments()[0];
  }
}
