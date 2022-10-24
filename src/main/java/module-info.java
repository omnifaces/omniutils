/*
 * Copyright 2021 OmniFaces
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
/**
 * @author Arjan Tijms
 */
module org.omnifaces.utils {

    exports org.omnifaces.utils;

    exports org.omnifaces.utils.annotation;
    exports org.omnifaces.utils.collection;
    exports org.omnifaces.utils.data;
    exports org.omnifaces.utils.exceptions;
    exports org.omnifaces.utils.function;
    exports org.omnifaces.utils.image;
    exports org.omnifaces.utils.io;
    exports org.omnifaces.utils.logging;
    exports org.omnifaces.utils.math;
    exports org.omnifaces.utils.properties;
    exports org.omnifaces.utils.reflect;
    exports org.omnifaces.utils.security;
    exports org.omnifaces.utils.stream;
    exports org.omnifaces.utils.text;
    exports org.omnifaces.utils.time;

    requires transitive java.desktop;
    requires transitive java.logging;

}



