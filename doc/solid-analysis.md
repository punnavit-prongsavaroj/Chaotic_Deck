# การวิเคราะห์ SOLID Principles ใน Game Engine - EternalClash2

## 1. Single Responsibility Principle (SRP)
- **หลักการ**: หนึ่งคลาสควรมีหน้าที่เดียวเท่านั้นและมีเหตุผลเดียวในการเปลี่ยนแปลง
- **ไฟล์**: `GameFacadeService.java`, `ActionValidator.java`
- **การประยุกต์ใช้**: `GameFacadeService` มีหน้าที่แค่เป็นตัวกลาง (Orchestrate) รวบรวมคำสั่งจาก Controller แล้วส่งต่อให้ Service ย่อยๆ ทำงาน โดยตัวมันเองจะไม่มี Business logic หนักๆ ส่วน `ActionValidator` แต่ละตัวจะรับผิดชอบการตรวจสอบเพียงเงื่อนไขเดียว เช่น `CoinBalanceValidator` เช็คแค่ว่าเหรียญพอไหม ถ้าจะแก้เงื่อนไขเงินก็มาแก้ที่ไฟล์เดียว

## 2. Open/Closed Principle (OCP)
- **หลักการ**: คลาสควรเปิดรับการขยาย (Extension) แต่ปิดการแก้ไข (Modification)
- **ไฟล์**: `GameActionStrategy.java`, `GameActionStrategyFactory.java`
- **การประยุกต์ใช้**: เมื่อเราต้องการเพิ่มการ์ดใหม่หรือ Action ใหม่ (เช่น ตัวละคร Inquisitor ในภาคเสริม) เราสามารถสร้างคลาสใหม่ `InquisitorAction` ที่ implement `GameActionStrategy` ได้เลย โดยไม่ต้องไปเพิ่ม `else if` หรือ `switch-case` ใน `GameActionStrategyFactory` เพราะเราใช้การ inject Map ของ Spring

## 3. Liskov Substitution Principle (LSP)
- **หลักการ**: คลาสลูกต้องสามารถแทนที่คลาสแม่ได้โดยไม่ทำให้โปรแกรมพัง (ไม่ throw UnsupportedOperationException)
- **ไฟล์**: `AbstractCharacterAction.java`, `IncomeAction.java`
- **การประยุกต์ใช้**: Action พื้นฐานอย่าง `IncomeAction` แม้จะไม่มีการท้า (Challenge) หรือล็อกเป้าหมาย (Target) แต่ก็สามารถทำงานผ่าน `execute(GameContext ctx)` ของ Interface ได้เหมือนกัน โดยที่ระบบเบื้องหลังสามารถประมวลผล Action ทุกชนิดผ่าน Interface เดียวอย่างปลอดภัย

## 4. Interface Segregation Principle (ISP)
- **หลักการ**: ไม่ควรบังคับให้คลาส implement interface ในสิ่งที่ไม่ได้ใช้งาน
- **ไฟล์**: `RoomReadService.java`, `RoomWriteService.java`
- **การประยุกต์ใช้**: แทนที่จะมี `RoomService` เดี่ยวๆ ที่มีทั้งการดึงข้อมูลและแก้ไข เราแยกเป็น Read/Write Interface เพื่อให้ Controller ที่มีหน้าที่แค่แสดงผล (เช่น หน้าล็อบบี้) ผูกติดกับ `RoomReadService` เท่านั้น ป้องกันการเผลอไปเรียกฟังก์ชันแก้ไขข้อมูลผิดพลาด

## 5. Dependency Inversion Principle (DIP)
- **หลักการ**: คลาสระดับสูงไม่ควรขึ้นอยู่กับคลาสระดับล่าง แต่ควรขึ้นอยู่กับ Interface
- **ไฟล์**: `GameFacadeService.java`, `DeckShuffler.java`
- **การประยุกต์ใช้**: ใน `GameFacadeService` เรา Inject ผ่าน Constructor เท่านั้น (ห้ามใช้ `@Autowired` ที่ field) และ Inject เป็น Interface (`RoomReadService`) ทำให้เวลาเขียน Test เราสามารถส่ง Mock Object เข้าไปได้ง่ายดาย หรือตอนสับการ์ด เราใช้ `DeckShuffler` เป็น Interface เพื่อซ่อน `SecureRandom` ไว้ ทำให้ระบบหลักไม่ต้องแคร์ว่าจะสุ่มด้วยเทคนิคใด
