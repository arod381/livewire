JAVA/GRADLE PROJECT USED AS AN ANDROID SHELL FOR AI SERVICES

Use IDE in order to change the file structure
from com/example/d308_mobile_application_development_android/
to   com/livewire/


PHASE 1 - Establish the new application architecture

Android UI
    |
ViewModel
    |
Repository
    |
AI Service


PHASE 2

                     USER
                      │
                      ▼
                 MainActivity
                      │
                 submitPrompt()
                      │
                      ▼
                MainViewModel
                      │
             repository.submitPrompt()
                      │
                      ▼
                MainRepository
                      │
                      │
                 returns result
                      │
                      ▼
                MainViewModel
                      │
                response.setValue()
                      │
                      ▼
                 LiveData
                      │
                      ▼
                MainActivity
                      │
                      ▼
                 TextView


LiveWire v2:

Using Official llama.cpp Android example, which is Kotlin-first and had to bridge Kotlin into existing Java codebase as java-llama.cpp hit 3 non-trivial build issues.

Merge, convert to GGUF, quantize
Same process as before: merge_and_unload(), patch tokenizer_config.json's extra_special_tokens if needed, convert to GGUF, quantize. Given mobile is now an explicit target, test more aggressive quantization levels (Q4_K_M and below) earlier than you did last time, since phone storage/RAM is the real constraint driving this choice.

Serve via llama.cpp on your server (stage 1: server testing)
Load the quantized GGUF via llama-cpp-python in server.py, same pattern as your current setup. This is the "server, after testing" stage — confirm correctness and response quality here before touching mobile deployment at all.

Integrate into the Java/Gradle app (stage 2: bundled/hard-coded model)
llama.cpp has an Android build path (JNI bindings via llama.android or similar community wrappers) that lets a GGUF model run inside a Java/Gradle app directly, no server round-trip. This is where model size really starts to bite — bundling a multi-hundred-MB-to-multi-GB GGUF file into an APK, plus on-device inference speed on phone CPUs (likely slower than your server, no GPU either), are the two things to validate early rather than late.

Full on-device operation (stage 3)
Once bundled inference is validated in the app, "raw on-device" mainly means removing any remaining server dependency (if your app currently falls back to server calls for anything) so the app is fully self-contained. This is more an integration/cleanup phase than new model work, assuming stage 2 already proved on-device inference works.

Fine-tune with LoRA/QLoRA (CPU, nohup)
Same LoRA/QLoRA approach as before, pointed at the new base model. Given the mobile end-goal, this is a good place to reconsider hyperparameters — a smaller base model may tolerate a higher LoRA rank within the same memory budget your server has, since the base itself is lighter.
