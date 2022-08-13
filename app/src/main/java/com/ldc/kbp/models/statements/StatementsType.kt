package com.ldc.kbp.models.statements

import androidx.fragment.app.Fragment
import likco.studyum.R
import com.ldc.kbp.fragments.statements.custom.FragmentWorkingOff
import com.ldc.kbp.ifFemale

enum class StatementsType(
    val statementName: String = "",
    val statementViewInfo: List<StatementViewInfo> = listOf(),
    val fragment: Fragment? = null
) {
    ABSENCE(
        "Отсутствие на занятиях",
        listOf(
            StatementViewInfo(
                "В связи с",
                Views.TEXT,
                "В связи с"
            ),
            StatementViewInfo(
                "Дата отсутствия",
                Views.DATE,
                "мне необходимо отсутствовать на учебных занятиях"
            )
        )
    ),
    ABSENCE_PARENTS(
        "Отсутствие для родителей",
        listOf(
            StatementViewInfo(
                "В связи с",
                Views.TEXT,
                "В связи с"
            ),
            StatementViewInfo(
                "Ф. И. О.",
                Views.TEXT,
                ifFemale("моей дочери", "моему сыну")
            ),
            StatementViewInfo(
                "Дата отсутствия",
                Views.DATE,
                "необходимо отсутствовать на учебных занятиях"
            )
        )
    ),
    EXPLANATION(
        "Объяснительная",
        listOf(
            StatementViewInfo(
                "Дата отсутствия",
                Views.DATE,
                "Я отсутствовал${ifFemale("a")} на учебных занятиях"
            ),
            StatementViewInfo(
                "В связи с",
                Views.TEXT,
                "в связи с"
            )
        )
    ),
    WORKING_OFF(
        "Отработка",
        fragment = FragmentWorkingOff()
    ),
    DUPLICATE(
        "Дубликат",
        listOf(
            StatementViewInfo(
                "",
                Views.SPINNER,
                "Прошу выдать мне дубликат",
                "в связи с утерей оригинала",
                spinnerArrayId = R.array.d_types
            )
        )
    ),
    PASS(
        "Пропуск",
        listOf(
            StatementViewInfo(
                "",
                Views.NONE,
                "Прошу выдать мне бесконтактную крату доступа в здание (карту-ключ) в связи с утерей."
            ),
            StatementViewInfo(
                "№ чека",
                Views.TEXT,
                "Кассовый чек об оплате №",
                nextParagraph = true
            ),
            StatementViewInfo(
                "Дата чека",
                Views.DATE,
                "от",
                "прилагается"
            )
        )
    ),
    TRANSFER(
        "Перевод на др. специальность",
        listOf(
            StatementViewInfo(
                "Со спец.",
                Views.TEXT,
                "Прошу перевесть меня со специальности"
            ),
            StatementViewInfo(
                "На спец.",
                Views.TEXT,
                "на специальность",
            ),
            StatementViewInfo(
                "Курс",
                Views.TEXT,
                "на"
            ),
            StatementViewInfo(
                "В связи с",
                Views.TEXT,
                "курс в связи с"
            )
        )
    ),
    ACADEMIC(
        "Предоставление академического отпуска",
        listOf(
            StatementViewInfo(
                "Со спец.",
                Views.TEXT,
                "Прошу предоставить мне академический отпуск по"
            ),
            StatementViewInfo(
                "На спец.",
                Views.TEXT,
                "на специальность",
            ),
            StatementViewInfo(
                "Курс",
                Views.TEXT,
                "на"
            ),
            StatementViewInfo(
                "В связи с",
                Views.TEXT,
                "курс в связи с"
            )
        )
    ),
    SURNAME(
        "Изменение фамилии",
        listOf(
            StatementViewInfo(
                "Пред. фамилия",
                Views.TEXT,
                "Прошу изменить мне фамилию с"
            ),
            StatementViewInfo(
                "Тек. фамилия",
                Views.TEXT,
                "на фамилию",
                "в связи со вступлением в брак"
            ),
            StatementViewInfo(
                "№ Свидет.",
                Views.TEXT,
                "Копия свидетельства о заключении брака",
                "прилагается",
                true
            )
        )
    ),
    INCREASE(
        "Повышение отметки",
        listOf(
            StatementViewInfo(
                "Предмет",
                Views.TEXT,
                "Прошу разрешить мне повторную аттестацию по учебной дисциплине"
            ),
            StatementViewInfo(
                "№ Семестра",
                Views.TEXT,
                "с целью повышения отметки за",
                "семестр"
            ),
            StatementViewInfo(
                "",
                Views.NONE,
                "За время получения образования в колледже учебные дисциплины с целью повышения отметки не пересдавал${
                    ifFemale("а")}"
            )
        )
    ),
    RECOVERY(
        "Восстановление",
        listOf(
            StatementViewInfo(
                "Специальность",
                Views.TEXT,
                "Прошу восстановить меня в число учащихся колледжа на дневную форму получения образования специальности"
            ),
            StatementViewInfo(
                "Специализация",
                Views.TEXT,
                "специализации"
            ),
            StatementViewInfo(
                "№ Семестра",
                Views.TEXT,
                "на"
            ),
            StatementViewInfo(
                "С",
                Views.DATE,
                "курс с"
            )
        )
    ),
    RECOVERY_ACADEMIC(
        "Восстановление из академ. отпуска",
        listOf(
            StatementViewInfo(
                "Специальность",
                Views.TEXT,
                "Прошу считать меня приступивше${
                    ifFemale(
                        "й",
                        "го"
                    )
                } к учебным занятиям дневной формы получения образования специальности"
            ),
            StatementViewInfo(
                "Специализация",
                Views.TEXT,
                "специализации"
            ),
            StatementViewInfo(
                "С",
                Views.DATE,
                "с",
                ", в связи с окончанием срока академического отпуска"
            )
        )
    )
}



