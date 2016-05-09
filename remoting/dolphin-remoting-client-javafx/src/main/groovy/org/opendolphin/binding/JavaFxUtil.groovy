/*
 * Copyright 2015-2016 Canoo Engineering AG.
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
package org.opendolphin.binding

import org.opendolphin.core.client.ClientAttributeWrapper
import javafx.scene.control.TableColumn
import javafx.util.Callback

class JavaFxUtil {

    /**
     * Register a cell value factory on the column that uses a ClientAttributeWrapper for
     * the property of the given name of the presentation model that represents the row data.
     * @return the column itself for convenient use in builders
     */
    static TableColumn value(String propertyName, TableColumn column) {
        column.cellValueFactory = { row -> new ClientAttributeWrapper(row.value[propertyName]) } as Callback
        return column
    }

    static Closure cellEdit(String propertyName, Closure convert ) {
        { event ->
            def positionPm = event.tableView.items.get(event.tablePosition.row)
            positionPm[propertyName].value = convert(event.newValue)
        }
    }
}