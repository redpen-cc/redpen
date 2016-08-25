/*
 * redpen: a text inspection tool
 * Copyright (c) 2014-2015 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
describe('setEditPosition', function() {

    var textarea;

    beforeEach(function() {
        $('body').empty();
        textarea = $('<textarea id="redpen-editor"></textarea>').val('This is is a pen.').appendTo('body');
    });

    it('setSelectionPosition', function() {
        var errors = [
            {
                "subsentence": {
                    "offset": 8,
                    "length": 2
                },
                "validator": "SuccessiveWord",
                "position": {
                    "start": {
                        "offset": 8,
                        "line": 1
                    },
                    "end": {
                        "offset": 10,
                        "line": 1
                    }
                },
                "message": "Found word \"is\" repeated twice in succession."
            }
        ]
        spyOn(textarea[0], 'setSelectionRange');
        RedPenUI.Utils.setEditPosition(errors[0]);
        expect(textarea[0].setSelectionRange).toHaveBeenCalledWith(10,10);
    });

    it('canPasteSampleText', function() {
        RedPenUI.sampleDocuments = {
            en: {
                parser: "PLAIN",
                document: "It is is cool!"
            },
            ja: {
                parser: "PLAIN",
                document: "そこにシビれる あこがれるゥ。"
            },
            en_md: {
                parser: "MARKDOWN", document: "# Instances\n" +
                "Soo cool!"
            },
            ja_md: {
                parser: "MARKDOWN",
                document: "# 分散処理\n"
            }
        }

        var viewValidator = $('<section id="redpen-view-validator">').appendTo('body');
        RedPenUI.pasteSampleText("en"); // plain
        expect("It is is cool!", viewValidator.val())

        RedPenUI.pasteSampleText("ja_md"); // markdown
        expect("# 分散処理", viewValidator.val())
    });

});
