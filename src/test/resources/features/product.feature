Feature: Add product

  @correct
  Scenario: Add a regular product
    Given I set up
    Given I am on the product page
    When I add a product with name "Морковь" and type "VEGETABLE" and "not exotic"
    Then the product "Морковь" should be added to the table

  @correct
  Scenario: Add an exotic fruit product
    Given I set up
    Given I am on the product page
    When I add a product with name "Банан" and type "FRUIT" and "exotic"
    Then the product "Банан" should be added to the table

  @correct
  Scenario: Add an exotic vegetable product
    Given I set up
    Given I am on the product page
    When I add a product with name "Свекла" and type "VEGETABLE" and "exotic"
    Then the product "Свекла" should be added to the table

  @correct
  Scenario: Add a regular fruit product
    Given I set up
    Given I am on the product page
    When I add a product with name "Клубника" and type "FRUIT" and "not exotic"
    Then the product "Клубника" should be added to the table
