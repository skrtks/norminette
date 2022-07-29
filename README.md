# Norminette for CLion

![Build](https://github.com/skrtks/norminette/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/17190-norminette.svg)](https://plugins.jetbrains.com/plugin/17190-norminette)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/17190-norminette.svg)](https://plugins.jetbrains.com/plugin/17190-norminette)

<!-- Plugin description -->

### Norminette v3.x is required
#### ✅ Verified Norminette version: v3.3.51
Please make sure that you have [Norminette](https://github.com/42School/norminette) installed!

### Issue reporting 🔬
If you find an issue please report it trough [GitHub](https://github.com/skrtks/norminette/issues/new/choose). Thanks!

### Setup 🛠
Check if Norminette is already installed on your system (run `norminette -v`). 
If not, you can download it [here](https://github.com/42School/norminette). 

When Norminette is properly installed on your system, Norminette for CLion should automatically detect the path to the executable.
If you have done a custom installation of Norminette, or the executable is not in PATH, please go to <kbd>Settings/Preferences</kbd> > <kbd>Norminette</kbd> 
and provide the path to your norminette executable.

It is possible to change the highlight style or enable/disable highlighting trough:
<kbd>Settings/Preferences</kbd> > <kbd>Editor</kbd> > <kbd>Inspections</kbd> > <kbd>C/C++</kbd> > <kbd>Norminette</kbd>

<!-- Plugin description end -->

### Plugin Installation

- Using IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "norminette"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/skrtks/norminette/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>
  
### Contributing
If you have a feature or fix that you would like to contribute, please take a look at the following steps:
1. Clone the repo: `git clone git@github.com:skrtks/norminette.git`
2. Create your fork `git remote add my-remote git@github.com:<username>/norminette.git`
3. Create a branch `git checkout -b my-branch`
4. Push the branch to your remote `git push --set-upstream my-remote my-branch`
5. Open a PR in GitHub
6. Someone (probably me) will review the changes, and merge the contribution into main
7. 🎉
