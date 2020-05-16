#!/bin/bash
#under msp everything should be identical on all computers
MACHINE_SPECIFIC_PATH="$HOME/Graduation_Studies/ThesisWork/HelperMethodAnalysis"

# Run all the driver generators
./classmate/driver_generator_classmate_1.5.1.sh
./commons_collections/driver_generator_commons_collections_4.3.sh
./commons_io/driver_generator_commons_io_2.5.sh
./commons_math/driver_generator_commons_math_3.6.1.sh
./gson/driver_generator_gson_parent_2.8.5.sh
./jackson_annotations/driver_generator_jackson_annotations_2.10.2.sh
./jsoup/driver_generator_jsoup_1.10.1.sh
./microbenchmark/driver_generator_microbenchmark.sh
./ph_commons/driver_generator_ph_commons_9.3.9.sh
./plexus_utils/driver_generator_plexus_utils_3.3.0.sh
