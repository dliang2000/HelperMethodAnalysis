#!/bin/bash
#under msp everything should be identical on all computers
MACHINE_SPECIFIC_PATH="$HOME/Graduation_Studies/ThesisWork/HelperMethodAnalysis"

# Run all the driver generators
./classmate/runjava_classmate_1.5.1.sh
./commons_collections/runjava_commons_collections_4.3.sh
./commons_io/runjava_commons_io_2.5.sh
./commons_math/runjava_commons_math_3.6.1.sh
./gson/runjava_gson_parent_2.8.5.sh
./jackson_annotations/runjava_jackson_annotations_2.10.2.sh
./jsoup/runjava_jsoup_1.10.1.sh
./microbenchmark/runjava_microbenchmark.sh
./ph_commons/runjava_ph_commons_9.3.9.sh
./plexus_utils/runjava_plexus_utils_3.3.0.sh
