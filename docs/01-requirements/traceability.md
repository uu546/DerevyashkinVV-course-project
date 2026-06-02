# 4. Таблица трассировки требований

Таблица трассировки показывает соответствие между бизнес-требованиями, актуальными Use Case и сущностями системы.

| Бизнес-требование | Use Case | Сущности | Приоритет |
|-------------------|----------|-----------|-----------|
| Управление номенклатурой товаров | UC-02, UC-03 | Product, Category, Unit | MUST |
| Приёмка товаров на склад (поставка) | UC-02 | Inventory, Movement, Location | MUST |
| Отгрузка товаров со склада | UC-03 | Inventory, Movement, Location | MUST |
| Перемещение товаров между ячейками | UC-04 | Inventory, Movement, Location | SHOULD |
| Просмотр актуальных остатков | — | Inventory, Product, Location | MUST |
| Авторизация и разграничение доступа | — | User | MUST |
| Формирование отчётности | — | Inventory, Movement | SHOULD |
| Массовые операции  | UC-02, UC-03 | Inventory, Movement | COULD |
| Интеграция со сканерами штрих-кодов | UC-02, UC-03 | Inventory, Movement | WON'T  |