# DiscountAsciiWarehouse
Android Exam for X-Team International

For the android exam you'll need to create a very simple app, called "Discount Ascii Warehouse".  There are some mockups to download here: https://drive.google.com/open?id=0BxGX6sRbP6SJMnNXMU1lUDVVczQ.  One shows the layout (search box, and result grid) and the other showing what each product in the result grid should look like.

You can get search results from the API with a request like this:

> curl http://74.50.59.155:5000/api/search

The API also accepts some parameters:

GET /api/search
Parameters
  ● limit (int) ­ Max number of search results to return
  ● skip (int) ­ Number of results to skip before sending back search results
  ● q (string)  - Search query. Tags separated by spaces.
  ● onlyInStock (bool)  - When flag is set, only return products that are currently in stock.

Response Type: NDJSON

The app should keep loading products from the API until it has enough to fill the screen, and then wait until the user has swiped to the bottom to load more.  The app should cache API requests for 1 hour.
