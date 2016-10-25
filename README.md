# ZMDB 
ZocDoc Movie Database. My implementation of ZocDoc's Mobile Engineer Take Home Evaluation. 

* Uses MVP via [Mosby](https://github.com/sockeqwe/mosby) for clean separation of UI from its data model and its presentation layer.
* Uses [Retrofit](https://github.com/square/retrofit) for its network stack and remote API layer.
* Uses [Picasso](https://github.com/square/picasso) for image downloading.
* Uses [Cache2K](https://github.com/cache2k/cache2k) for rudimentary runtime caching complete with short lived values.

##TODO:
* Integrate SQLite Database library for persisting "AllMovies" response data.
* Building a filter UI so that it can be filtered from the persisted data.
