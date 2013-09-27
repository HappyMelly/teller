# How to contribute
Third-party patches are essential for keeping HM Teller great. We grow HM Teller
and [Happy Melly](http://happymelly.com) together and simply cannot make many
cool features due to obvious limitations of time and business demand. We want
to keep it as easy as possible to contribute changes that add new features or
behaviour essential for your business. There are a few guidelines that we need
contributors to follow so that we can have a chance of keeping on top of things.

## Quick guide
Four simple steps to add changes:

1. Fork the repo.

2. Add a test for your change. _We know we have no tests right now. We'll fix
this soon._

3. Make the test pass.

4. Push to your fork and submit a pull request.

At this point you're waiting on us. We like to at least comment on. We may
suggest some changes or improvements or alternatives.

## Making changes

* Please avoid working directly on `master` branch. We do not use release branches
and keep `master` in a ready-to-deploy state
* Check for unnecessary whitespace with `git diff --check` before committing
* Include tests that fail without your code, and pass with it
* Update the documentation, the surrounding one, examples elsewhere, guides,
  whatever is affected by your contribution

# Additional Resources

* [Google mailing list for HM Teller contributors](https://groups.google.com/d/forum/happymelly-teller)
* [General GitHub documentation](http://help.github.com/)
* [GitHub pull request documentation](http://help.github.com/send-pull-requests/)
