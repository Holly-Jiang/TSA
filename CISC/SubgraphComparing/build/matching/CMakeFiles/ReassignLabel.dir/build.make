# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.10

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:


#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:


# Remove some rules from gmake that .SUFFIXES does not remove.
SUFFIXES =

.SUFFIXES: .hpux_make_needs_suffix_list


# Suppress display of executed commands.
$(VERBOSE).SILENT:


# A target that is always out of date.
cmake_force:

.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /usr/bin/cmake

# The command to remove a file.
RM = /usr/bin/cmake -E remove -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /home/jh/github/TSA/CISC/SubgraphComparing

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /home/jh/github/TSA/CISC/SubgraphComparing/build

# Include any dependencies generated for this target.
include matching/CMakeFiles/ReassignLabel.dir/depend.make

# Include the progress variables for this target.
include matching/CMakeFiles/ReassignLabel.dir/progress.make

# Include the compile flags for this target's objects.
include matching/CMakeFiles/ReassignLabel.dir/flags.make

matching/CMakeFiles/ReassignLabel.dir/ReassignLabel.cpp.o: matching/CMakeFiles/ReassignLabel.dir/flags.make
matching/CMakeFiles/ReassignLabel.dir/ReassignLabel.cpp.o: ../matching/ReassignLabel.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/jh/github/TSA/CISC/SubgraphComparing/build/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building CXX object matching/CMakeFiles/ReassignLabel.dir/ReassignLabel.cpp.o"
	cd /home/jh/github/TSA/CISC/SubgraphComparing/build/matching && /usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/ReassignLabel.dir/ReassignLabel.cpp.o -c /home/jh/github/TSA/CISC/SubgraphComparing/matching/ReassignLabel.cpp

matching/CMakeFiles/ReassignLabel.dir/ReassignLabel.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/ReassignLabel.dir/ReassignLabel.cpp.i"
	cd /home/jh/github/TSA/CISC/SubgraphComparing/build/matching && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/jh/github/TSA/CISC/SubgraphComparing/matching/ReassignLabel.cpp > CMakeFiles/ReassignLabel.dir/ReassignLabel.cpp.i

matching/CMakeFiles/ReassignLabel.dir/ReassignLabel.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/ReassignLabel.dir/ReassignLabel.cpp.s"
	cd /home/jh/github/TSA/CISC/SubgraphComparing/build/matching && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/jh/github/TSA/CISC/SubgraphComparing/matching/ReassignLabel.cpp -o CMakeFiles/ReassignLabel.dir/ReassignLabel.cpp.s

matching/CMakeFiles/ReassignLabel.dir/ReassignLabel.cpp.o.requires:

.PHONY : matching/CMakeFiles/ReassignLabel.dir/ReassignLabel.cpp.o.requires

matching/CMakeFiles/ReassignLabel.dir/ReassignLabel.cpp.o.provides: matching/CMakeFiles/ReassignLabel.dir/ReassignLabel.cpp.o.requires
	$(MAKE) -f matching/CMakeFiles/ReassignLabel.dir/build.make matching/CMakeFiles/ReassignLabel.dir/ReassignLabel.cpp.o.provides.build
.PHONY : matching/CMakeFiles/ReassignLabel.dir/ReassignLabel.cpp.o.provides

matching/CMakeFiles/ReassignLabel.dir/ReassignLabel.cpp.o.provides.build: matching/CMakeFiles/ReassignLabel.dir/ReassignLabel.cpp.o


# Object files for target ReassignLabel
ReassignLabel_OBJECTS = \
"CMakeFiles/ReassignLabel.dir/ReassignLabel.cpp.o"

# External object files for target ReassignLabel
ReassignLabel_EXTERNAL_OBJECTS =

matching/ReassignLabel: matching/CMakeFiles/ReassignLabel.dir/ReassignLabel.cpp.o
matching/ReassignLabel: matching/CMakeFiles/ReassignLabel.dir/build.make
matching/ReassignLabel: matching/CMakeFiles/ReassignLabel.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir=/home/jh/github/TSA/CISC/SubgraphComparing/build/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Linking CXX executable ReassignLabel"
	cd /home/jh/github/TSA/CISC/SubgraphComparing/build/matching && $(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/ReassignLabel.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
matching/CMakeFiles/ReassignLabel.dir/build: matching/ReassignLabel

.PHONY : matching/CMakeFiles/ReassignLabel.dir/build

matching/CMakeFiles/ReassignLabel.dir/requires: matching/CMakeFiles/ReassignLabel.dir/ReassignLabel.cpp.o.requires

.PHONY : matching/CMakeFiles/ReassignLabel.dir/requires

matching/CMakeFiles/ReassignLabel.dir/clean:
	cd /home/jh/github/TSA/CISC/SubgraphComparing/build/matching && $(CMAKE_COMMAND) -P CMakeFiles/ReassignLabel.dir/cmake_clean.cmake
.PHONY : matching/CMakeFiles/ReassignLabel.dir/clean

matching/CMakeFiles/ReassignLabel.dir/depend:
	cd /home/jh/github/TSA/CISC/SubgraphComparing/build && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /home/jh/github/TSA/CISC/SubgraphComparing /home/jh/github/TSA/CISC/SubgraphComparing/matching /home/jh/github/TSA/CISC/SubgraphComparing/build /home/jh/github/TSA/CISC/SubgraphComparing/build/matching /home/jh/github/TSA/CISC/SubgraphComparing/build/matching/CMakeFiles/ReassignLabel.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : matching/CMakeFiles/ReassignLabel.dir/depend

