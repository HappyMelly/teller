[![Coverage Status](https://coveralls.io/repos/HappyMelly/teller/badge.svg?branch=master)](https://coveralls.io/r/HappyMelly/teller?branch=master)

# Happy Melly Teller

**Happy Melly Teller** is a web system which supports [Happy Melly](http://happymelly.com)'s
ecosystem and perfectly suits for any business or non-profit organization
with similar network structure.

The platform allows to manage information about people, organizations, brands,
licensees and so on. It also provides REST API for retrieving these objects
from third-party apps and websites.

## How to install and run

### Pre-requisites

1. Install Vagrant. Follow this guide from their official site: [https://docs.vagrantup.com/v2/installation/index.html](Installing Vagrant)

2. Create and setup Twitter application
    * Create a new application on [https://dev.twitter.com](https://dev.twitter.com)
    * Add callback URL `http://127.0.0.1:9000/authenticate/twitter`
    * Set checkbox **Allow this application to be used to Sign in with Twitter**

### Application
1. Clone the repo, (for the meantime pull the branch that works with vagrant) ..

2. Supply the needed values for the following:

    ```
      > cd teller/vagrant
      > vim up.bash
      ...
      # hm-teller specific variables
      aws_key=
      aws_secret=
      fb_app_id=
      fb_secret=
      google_app_id=
      google_secret=
      linkedin_key=
      linkedin_secret=
      twitter_key=
      twitter_secret=
      memcached_url=127.0.0.1:112111
      memcached_username=happymelly
      memcached_password=
      ...
    ```

    Save your changes then go back to the root directory (ie teller)

    ```
      > cd teller
    ```

2. Run the application.
  Start vagrant (vagrant up) then tunnel your way with ssh(vagrant ssh). From inside vagrant,
  you can run  `sbt run` .

    ```
        > vagrant up
        > vagrant ssh
        #.. inside vagrant
        vagrant@hm-teller:/ > cd /vagrant
        vagrant@hm-teller:/ > sbt run
    ```

3. Open the application in a web browser and run Evolutions to populate the database
    * open [http://localhost:9000](http://localhost:9000)
    * on the _Database 'default' needs evolution!_ page, click _Apply this script now!_

4. Update database (required to give you access by your twitter account).

  ```
    > vagrant ssh
    vagrant@hm-teller > mysql -u melly -pshum
    mysql > update user_account set TWITTER_HANDLE="[your twitter id]" where TWITTER_HANDLE="skotlov";
  ```

5. Time to log in, pal!

## I have an idea/I want report a bug

Please, [open an issue](https://github.com/HappyMelly/teller/issues), provide as
much related information as possible and stay tuned :).

## I want to help

Check [notes for contributors](https://github.com/HappyMelly/teller/blob/master/CONTRIBUTING.md)

## License

Happy Melly Teller is licensed under [GNU General Public License v.3](http://www.gnu.org/copyleft/gpl.html).
It includes [iText library](http://itextpdf.com) which is licensed under [AGPL](http://www.gnu.org/licenses/agpl-3.0.html).
