import {Center, VStack} from '@/theme';
import NGLogoutButton from '@/components/ng-button/NGLogoutButton';
import NGText from '@components/ng-text/NGText';
function Setting() {
  return (
    <Center sx={{height: '100vh'}}>
      <VStack>
        <NGText text={'Setting page'} />
        <NGLogoutButton />
      </VStack>
    </Center>
  );
}

export default Setting;
