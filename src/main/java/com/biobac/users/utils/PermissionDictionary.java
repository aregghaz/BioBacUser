package com.biobac.users.utils;

import java.util.Map;

public final class PermissionDictionary {

    private PermissionDictionary() {
    }

    public static final Map<String, String> ENTITY_RU = Map.ofEntries(
            Map.entry("OUR_COMPANY", "Наша компания"),
            Map.entry("CONTACT_PERSON", "Контактное лицо"),
            Map.entry("ACCOUNTS", "Счет"),
            Map.entry("PAYMENT_CATEGORY", "Категория оплаты"),
            Map.entry("PRICE", "Прайс-лист"),
            Map.entry("USER", "Пользователь"),
            Map.entry("WAREHOUSE", "Склад"),
            Map.entry("PRODUCT", "Продукт"),
            Map.entry("INGREDIENT", "Ингредиент"),
            Map.entry("INGREDIENT_COMPLETED_DEAL", "Завершённая сделка"),
            Map.entry("INGREDIENT_NOT_COMPLETED_DEAL", "Незавершённая сделка"),
            Map.entry("INGREDIENT_GROUP", "Группа ингредиентов"),
            Map.entry("RECIPE_ITEM", "Рецепт"),
            Map.entry("PRODUCT_HISTORY", "История продукта"),
            Map.entry("INGREDIENT_HISTORY", "История ингредиента"),
            Map.entry("UNIT", "Единица измерения"),
            Map.entry("UNIT_TYPE", "Тип единицы"),
            Map.entry("POSITION", "Должность"),
            Map.entry("PERMISSION", "Разрешение"),
            Map.entry("COMPANY", "Компания"),
            Map.entry("ATTRIBUTE", "Атрибут"),
            Map.entry("ATTRIBUTE_GROUP", "Группа атрибутов"),
            Map.entry("COMPANY_TYPE", "Тип компании"),
            Map.entry("COMPANY_BUYER", "Покупатели"),
            Map.entry("COMPANY_SELLER", "Поставщики"),

            Map.entry("REGION", "Регион"),
            Map.entry("COMPANY_SALE_TYPE", "Тип продаж компании"),
            Map.entry("ASSET", "Основные средства"),
            Map.entry("ASSET_CATEGORY", "Категория основных средств"),
            Map.entry("ASSET_IMPROVEMENT", "Улучшение основных средств"),
            Map.entry("DEPARTMENT", "Отдел"),
            Map.entry("ACCOUNT", "Аккаунт"),
            Map.entry("DEPRECIATION_RECORD", "Запись амортизации"),
            Map.entry("EXPENSE_TYPE", "Тип расхода"),

            Map.entry("INGREDIENT_BALANCE", "Баланс ингредиентов"),
            Map.entry("INGREDIENT_DETAIL", "Деталь ингредиента"),
            Map.entry("MANUFACTURE_PRODUCT", "Производимый продукт"),
            Map.entry("PRODUCT_BALANCE", "Баланс продукта"),
            Map.entry("PRODUCT_DETAIL", "Деталь продукта"),
            Map.entry("PRODUCT_GROUP", "Группа продуктов"),

            Map.entry("RECEIVE_EXPENSE", "Расходы закупки"),
            Map.entry("RECEIVE_INGREDIENT", "Поступления ингредиента"),

            Map.entry("WAREHOUSE_GROUP", "Группа складов"),
            Map.entry("WAREHOUSE_TYPE", "Тип склада"),

            Map.entry("COMPANY_GROUP", "Группа компаний"),

            Map.entry("COMPONENT_TRANSFER", "Перемещение между складами"),
            Map.entry("COMPONENT_INVENTORIZATION", "Инвентаризация")
    );

    public static final Map<String, String> OPERATION_RU = Map.of(
            "READ", "Просмотр",
            "CREATE", "Создание",
            "UPDATE", "Редактирование",
            "DELETE", "Удаление"
    );

    public static final Map<String, String> SPECIAL_PERMISSIONS = Map.ofEntries(
            Map.entry("RECEIVE_INGREDIENT_STATUS_UPDATE", "Изменение статуса поступления ингредиента"),
            Map.entry("RECEIVE_INGREDIENT_QUANTITY_UPDATE", "Изменение количества поступления ингредиента"),
            Map.entry("INGREDIENT_ENTRY_EXPENSE_UPDATE", "Обновление расходов для закупки"),

            Map.entry("EMPLOYEE_FIRED_READ", "Просмотр уволенных сотрудников"),
            Map.entry("EMPLOYEE_HISTORY_READ", "Просмотр истории сотрудников"),
            Map.entry("ACCOUNTS_TRANSFER", "Переводы между счетами"),
            Map.entry("PAYMENT_CREATE", "Создание платежей"),
            Map.entry("PAYMENT_HISTORY_READ", "Просмотр истории платежей"),
            Map.entry("SALE_PAYMENT_READ", "Просмотр платежей по продажам"),
            Map.entry("SALE_COMPLETED_READ", "Просмотр завершённых продаж"),
            Map.entry("SALE_NOT_COMPLETED_READ", "Просмотр незавершённых продаж "),

            Map.entry("COMPONENT_INGREDIENT_TRANSFER", "Перемещение ингредиентов между складами"),
            Map.entry("COMPONENT_PRODUCT_TRANSFER", "Перемещение продуктов между складами"),
            Map.entry("COMPONENT_INGREDIENT_TRANSFER_READ", "Просмотр перемещений ингредиентов"),
            Map.entry("COMPONENT_PRODUCT_TRANSFER_READ", "Просмотр перемещений продуктов"),

            Map.entry("COMPONENT_INGREDIENT_INVENTORIZATION", "Инвентаризация ингредиентов"),
            Map.entry("COMPONENT_PRODUCT_INVENTORIZATION", "Инвентаризация продуктов"),
            Map.entry("COMPONENT_INGREDIENT_INVENTORIZATION_READ", "Просмотр инвентаризации ингредиентов"),
            Map.entry("COMPONENT_PRODUCT_INVENTORIZATION_READ", "Просмотр инвентаризации продуктов")
    );

    public static final Map<String, String> SPECIAL_GROUPS = Map.ofEntries(
            Map.entry("RECEIVE_INGREDIENT_STATUS_UPDATE", "Поступления ингредиента"),
            Map.entry("RECEIVE_INGREDIENT_QUANTITY_UPDATE", "Поступления ингредиента"),
            Map.entry("INGREDIENT_ENTRY_EXPENSE_UPDATE", "Расходы закупки"),
            Map.entry("ALL_GROUP_ACCESS", "Общий доступ"),

            Map.entry("COMPONENT_INGREDIENT_TRANSFER", "Перемещения"),
            Map.entry("COMPONENT_PRODUCT_TRANSFER", "Перемещения"),
            Map.entry("COMPONENT_INGREDIENT_TRANSFER_READ", "Перемещения"),
            Map.entry("COMPONENT_PRODUCT_TRANSFER_READ", "Перемещения"),

            Map.entry("COMPONENT_INGREDIENT_INVENTORIZATION", "Инвентаризация"),
            Map.entry("COMPONENT_PRODUCT_INVENTORIZATION", "Инвентаризация"),
            Map.entry("COMPONENT_INGREDIENT_INVENTORIZATION_READ", "Инвентаризация"),
            Map.entry("COMPONENT_PRODUCT_INVENTORIZATION_READ", "Инвентаризация")
    );
}
